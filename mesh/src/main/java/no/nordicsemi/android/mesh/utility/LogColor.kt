package no.nordicsemi.android.mesh.utility

import java.lang.StringBuilder


/**
 * Created by Khoa Nguyen on 8/20/18.
 * Copyright (c) 2018. All rights reserved.
 * Email: khoantt91@gmail.com
 */
@Suppress("MayBeConstant", "unused")
object LogColor {

    val RESET = "\u001b[0m"  // Text Reset

    /* Regular Colors */
    val BLACK = "\u001b[0;30m"   // BLACK
    val RED = "\u001b[0;31m"     // RED
    val GREEN = "\u001b[0;32m"   // GREEN
    val YELLOW = "\u001b[0;33m"  // YELLOW
    val BLUE = "\u001b[0;34m"    // BLUE
    val PURPLE = "\u001b[0;35m"  // PURPLE
    val CYAN = "\u001b[0;36m"    // CYAN
    val WHITE = "\u001b[0;37m"   // WHITE
    val RED_LIGHT = "\u001b[31;1m"
    val ORGANE = "\u001b[38;5;172m"
    val PURPLE_LIGHT = "\u001b[38;5;199m"
    val BLUE_LIGHT = "\u001b[38;5;26m"
    val GREEN_LIGHT = "\u001b[38;5;70m"

    /* Bold */
    val BLACK_BOLD = "\u001b[1;30m"  // BLACK
    val RED_BOLD = "\u001b[1;31m"    // RED
    val GREEN_BOLD = "\u001b[1;32m"  // GREEN
    val YELLOW_BOLD = "\u001b[1;33m" // YELLOW
    val BLUE_BOLD = "\u001b[1;34m"   // BLUE
    val PURPLE_BOLD = "\u001b[1;35m" // PURPLE
    val CYAN_BOLD = "\u001b[1;36m"   // CYAN
    val WHITE_BOLD = "\u001b[1;37m"  // WHITE

    /* Underline */
    val BLACK_UNDERLINED = "\u001b[4;30m"  // BLACK
    val RED_UNDERLINED = "\u001b[4;31m"    // RED
    val GREEN_UNDERLINED = "\u001b[4;32m"  // GREEN
    val YELLOW_UNDERLINED = "\u001b[4;33m" // YELLOW
    val BLUE_UNDERLINED = "\u001b[4;34m"   // BLUE
    val PURPLE_UNDERLINED = "\u001b[4;35m" // PURPLE
    val CYAN_UNDERLINED = "\u001b[4;36m"   // CYAN
    val WHITE_UNDERLINED = "\u001b[4;37m"  // WHITE

    /* Background */
    val BLACK_BACKGROUND = "\u001b[40m"  // BLACK
    val RED_BACKGROUND = "\u001b[41m"    // RED
    val GREEN_BACKGROUND = "\u001b[42m"  // GREEN
    val YELLOW_BACKGROUND = "\u001b[43m" // YELLOW
    val BLUE_BACKGROUND = "\u001b[44m"   // BLUE
    val PURPLE_BACKGROUND = "\u001b[45m" // PURPLE
    val CYAN_BACKGROUND = "\u001b[46m"   // CYAN
    val WHITE_BACKGROUND = "\u001b[47m"  // WHITE

    /* High Intensity */
    val BLACK_BRIGHT = "\u001b[0;90m"  // BLACK
    val RED_BRIGHT = "\u001b[0;91m"    // RED
    val GREEN_BRIGHT = "\u001b[0;92m"  // GREEN
    val YELLOW_BRIGHT = "\u001b[0;93m" // YELLOW
    val BLUE_BRIGHT = "\u001b[0;94m"   // BLUE
    val PURPLE_BRIGHT = "\u001b[0;95m" // PURPLE
    val CYAN_BRIGHT = "\u001b[0;96m"   // CYAN
    val WHITE_BRIGHT = "\u001b[0;97m"  // WHITE

    /* Bold High Intensity */
    val BLACK_BOLD_BRIGHT = "\u001b[1;90m" // BLACK
    val RED_BOLD_BRIGHT = "\u001b[1;91m"   // RED
    val GREEN_BOLD_BRIGHT = "\u001b[1;92m" // GREEN
    val YELLOW_BOLD_BRIGHT = "\u001b[1;93m"// YELLOW
    val BLUE_BOLD_BRIGHT = "\u001b[1;94m"  // BLUE
    val PURPLE_BOLD_BRIGHT = "\u001b[1;95m"// PURPLE
    val CYAN_BOLD_BRIGHT = "\u001b[1;96m"  // CYAN
    val WHITE_BOLD_BRIGHT = "\u001b[1;97m" // WHITE

    /* High Intensity backgrounds */
    val BLACK_BACKGROUND_BRIGHT = "\u001b[0;100m"// BLACK
    val RED_BACKGROUND_BRIGHT = "\u001b[0;101m"// RED
    val GREEN_BACKGROUND_BRIGHT = "\u001b[0;102m"// GREEN
    val YELLOW_BACKGROUND_BRIGHT = "\u001b[0;103m"// YELLOW
    val BLUE_BACKGROUND_BRIGHT = "\u001b[0;104m"// BLUE
    val PURPLE_BACKGROUND_BRIGHT = "\u001b[0;105m" // PURPLE
    val CYAN_BACKGROUND_BRIGHT = "\u001b[0;106m"  // CYAN
    val WHITE_BACKGROUND_BRIGHT = "\u001b[0;107m"   // WHITE

    val DEVICE_HTTP = "\u001b[38;5;166m"

    fun colorLog(colorLog: String, string: String): String {
        val stringBuilder = StringBuilder(string)
        val stringBuilderTemp = StringBuilder(colorLog)
        stringBuilder.forEach {
            stringBuilderTemp.append(it)
            if (it == '\n') stringBuilderTemp.append(colorLog)
        }
        return stringBuilderTemp.toString()
    }

    fun testPrintColorCode() {
        for (i in 0 until 16) {
            for (j in 0 until 16) {
                val code = "${i * 16 + j}"
                DebugLog.d("\u001b[38;5;${code}m  :$code")
            }
        }
    }

}