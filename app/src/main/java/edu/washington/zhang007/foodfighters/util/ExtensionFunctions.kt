package edu.washington.zhang007.foodfighters.util

import java.util.*

/*
Extension function to generate a random number inside a range.
Stolen from internet.
 */
fun ClosedRange<Int>.random() =
        Random().nextInt(endInclusive - start) +  start
