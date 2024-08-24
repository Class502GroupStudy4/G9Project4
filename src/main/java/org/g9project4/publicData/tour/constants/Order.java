package org.g9project4.publicData.tour.constants;

import lombok.Getter;

import java.util.List;

@Getter
public enum Order {
    //정렬 이름순, 최신순, 거리순, 인기순
    title("이름순","title"),
    updateDate("최신순","updateDate"),
    distance("거리순","distance"),
    popularity("인기순","popularity"),;
    private final String type;
    private final String name;

    Order(String type, String name) {
        this.type = type;
        this.name = name;
    }
    public static List<Order> getOrders() {
        return List.of(Order.values());
    }
}
