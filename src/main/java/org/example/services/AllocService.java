package org.example.services;

import org.example.models.AllocationType;
import org.example.models.Order;
import org.example.models.PaymentMethod;

import java.util.*;
import java.util.stream.Collectors;

public class AllocService {

    public List<AllocationType> allocate(List<Order> orders, List<PaymentMethod> methods) {
        Map<String, PaymentMethod> pmMap = methods.stream()
                .collect(Collectors.toMap(
                        PaymentMethod::getId,
                        pm -> new PaymentMethod(
                                pm.getId(),
                                String.valueOf(pm.getDiscountPercentage()),
                                String.format("%.2f", pm.getLimitInCents() / 100.0))
                ));

        List<AllocationType> allocations = new ArrayList<>();
        Set<String> assigned = new HashSet<>();

        List<PaymentMethod> promos = pmMap.values().stream()
                .filter(pm -> !"PUNKTY".equals(pm.getId()))
                .sorted(Comparator.comparingInt(PaymentMethod::getDiscountPercentage).reversed())
                .collect(Collectors.toList());

        for (PaymentMethod promo : promos) {
            String pid = promo.getId();
            long pct = promo.getDiscountPercentage();
            Optional<Order> best = orders.stream()
                    .filter(o -> !assigned.contains(o.getId()))
                    .filter(o -> o.getPromotions().contains(pid))
                    .max(Comparator.comparingLong(o -> o.getValueInCents() * pct / 100));

            if (best.isPresent()) {
                Order o = best.get();
                long value = o.getValueInCents();
                long discount = value * pct / 100;
                long net = value - discount;
                allocations.add(new AllocationType(o.getId(), pid, net, 0, net));
                promo.decreaseLimit(net);
                assigned.add(o.getId());
            }
        }

        PaymentMethod points = pmMap.get("PUNKTY");
        if (points != null) {
            orders.stream()
                    .filter(o -> !assigned.contains(o.getId()))
                    .sorted(Comparator.comparingLong(Order::getValueInCents).reversed())
                    .forEach(o -> {
                        long value = o.getValueInCents();
                        long discount = value * points.getDiscountPercentage() / 100;
                        long net = value - discount;
                        if (points.getLimitInCents() >= net) {
                            allocations.add(new AllocationType(o.getId(), "PUNKTY", net, net, 0));
                            points.decreaseLimit(net);
                            assigned.add(o.getId());
                        }
                    });
        }

        for (Order o : orders) {
            if (assigned.contains(o.getId())) continue;

            long value = o.getValueInCents();
            long minPts = value / 10;
            if (points == null || points.getLimitInCents() < minPts) {
                throw new IllegalStateException("Can't process order: " + o.getId());
            }

            long usePts = Math.max(points.getLimitInCents() >= minPts ? minPts : 0, Math.min(points.getLimitInCents(), value));

            long net = value * 90 / 100;

            allocations.add(new AllocationType(o.getId(), "PUNKTY", 0, usePts, 0));
            points.decreaseLimit(usePts);

            long remaining = net - usePts;
            List<PaymentMethod> cards = pmMap.values().stream()
                    .filter(c -> !"PUNKTY".equals(c.getId()))
                    .sorted(Comparator.comparingLong(PaymentMethod::getLimitInCents).reversed())
                    .toList();

            for (PaymentMethod card : cards) {
                if (remaining <= 0) break;
                long avail = card.getLimitInCents();
                if (avail <= 0) continue;
                long used = Math.min(avail, remaining);
                allocations.add(new AllocationType(o.getId(), card.getId(), used, 0, used));
                card.decreaseLimit(used);
                remaining -= used;
            }

            if (remaining > 0) {
                throw new IllegalStateException("Insufficient funds for order: " + o.getId());
            }
            assigned.add(o.getId());
        }

        return allocations;
    }
}
