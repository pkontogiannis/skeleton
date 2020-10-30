package com.skeleton.service.multimedia

class MultimediaModel {

  case class Multimedia(
                         id: Option[Int] = None,
                         multemediaPath: String,
                         contentType: String,
                         created_on: Date,
                         uploader: Int
                       )

}
