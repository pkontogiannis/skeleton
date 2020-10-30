package com.skeleton.service.apartment

object ApartmentModel {

  case class Apartment(
                        id: Option[Int] = None,
                        apartmentName: String,
                        buildingId: Int,
                        floor: Int,
                        area: Float
                      )

}
