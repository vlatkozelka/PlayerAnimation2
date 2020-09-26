package com.example.playeranimation2

import androidx.annotation.DrawableRes
import java.util.*

/**
 * Created by Ali on 9/26/2020.
 */
data class Song(
    @DrawableRes val imageRes: Int,
    val id: String = UUID.randomUUID().toString(),
    val title: String
) {

  companion object {
    fun getRandomSongs(): List<Song> {
      return listOf(
          Song(
              imageRes = R.drawable.car_bomb,
              title = "car bomb"
          ),
          Song(
              imageRes = R.drawable.car_bomb_meta,
              title = "car bomb meta"
          ),
          Song(
              imageRes = R.drawable.car_bomb_mordial,
              title = "car bomb mordial"
          )
      )
    }
  }


}