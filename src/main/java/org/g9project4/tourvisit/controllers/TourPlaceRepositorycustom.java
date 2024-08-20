package org.g9project4.tourvisit.controllers;

import org.g9project4.publicData.tour.entities.TourPlace;

import java.util.List;

public interface TourPlaceRepositorycustom {

    List<TourPlace> getTourPlacesPoint(String contentTypeId);
}
