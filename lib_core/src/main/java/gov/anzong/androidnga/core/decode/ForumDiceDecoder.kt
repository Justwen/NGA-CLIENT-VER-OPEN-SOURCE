package gov.anzong.androidnga.core.decode

import gov.anzong.androidnga.common.base.JavaBean
import gov.anzong.androidnga.core.data.HtmlData
import java.util.regex.Pattern
import kotlin.math.floor

// 计算掷骰子结果
class ForumDiceDecoder : IForumDecoder {

    class DiceData : JavaBean {
        var txt: String? = null
        var authorId = 0
        var tid = 0
        var pid = 0
        var seedOffset = 0
        var rndSeed = 0.0
        var id: String? = null
        var argsId: String? = null
        var seed = 0.0
    }

    override fun decode(content: String?, htmlData: HtmlData?): String {
        val diceData = buildDiceData(htmlData!!)
        return getRealDice(diceData, content!!)
    }


    private fun buildDiceData(htmlData: HtmlData): DiceData? {
        if (htmlData.uid == null || htmlData.pid == null || htmlData.tid == null) {
            return null
        }
        val arg = DiceData()
        arg.seed = 2110032.0
        arg.authorId = htmlData.uid.toIntOrNull() ?: 0
        arg.pid = htmlData.pid.toIntOrNull() ?: 0
        arg.tid = htmlData.tid.toIntOrNull() ?: 0
        arg.id = "postcontent0"
        val argsId =
            if (arg.id != null) {
                arg.id
            } else {
                randDigit("bbcode", 10000)
            }
        arg.argsId = argsId
        return arg
    }

    private fun randDigit(p: String, l: Int): String {
        return p + floor(Math.random() * l)
    }

    private fun getRealDice(arg: DiceData?, content: String): String {
        if (arg == null) {
            return content
        }
        val reg = "\\[dice].+?\\[/dice]"
        var sum = 0
        var txt = content
        val pattern = Pattern.compile(reg)
        var matcher = pattern.matcher(content)
        if (!matcher.find()) {
            return content
        }
        do {
            val diceStr = StringBuilder()
            val group = matcher.group(0)?.replace("[dice]", "")?.replace("[/dice]", "")
            val newGroup = "+$group"
            val rx = StringBuilder()
            for (str in newGroup.split("\\+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                if (str.isNotEmpty()) {
                    val sstrs = str.split("d")
                    var num: Int
                    var covers: Int
                    if (sstrs.size > 1) {
                        num = if (sstrs[0].isNotEmpty()) {
                            sstrs[0].toIntOrNull() ?: 1
                        } else {
                            1
                        }
                        covers = sstrs[1].toIntOrNull() ?: 0
                        if (num > 10 || covers > 100000) {
                            sum = -1
                            diceStr.append("+OUT OF LIMIT")
                        }
                        for (j in 0 until num) {
                            val argsId = "postcomment__510458140"
                            arg.argsId = argsId
                            val a: Double = rnd(arg)
                            val rand = floor(a * covers) + 1
                            rx.append("+d").append(covers).append("(").append(Math.round(rand))
                                .append(")")
                            if (sum != -1) {
                                sum += rand.toInt()
                            }
                        }
                    } else {
                        covers = sstrs[0].trim().toIntOrNull() ?: 0
                        sum += covers
                        rx.append("+").append(covers)
                    }
                }
            }
            diceStr.append("<p><b>ROLL:").append(group).append("</b>=").append(rx.substring(1))
                .append("=<b>").append(sum).append("</b></p>")
            sum = 0
            txt = txt.replaceFirst(reg.toRegex(), diceStr.toString())
            matcher = pattern.matcher(txt)
        } while (matcher.find())
        return txt
    }

    private fun rnd(arg: DiceData): Double {
        var seed: Double = arg.seed
        if (arg.argsId != null) {
            if (arg.rndSeed == 0.0) {
                arg.rndSeed = (
                        (arg.authorId + arg.tid + arg.pid +
                                (if (arg.tid > 10246184 || arg.pid > 200188932) arg.seedOffset else 0)).toDouble()
                        )
                if (arg.rndSeed == 0.0) arg.rndSeed = floor(Math.random() * 10000)
            }
            arg.rndSeed = ((arg.rndSeed * 9301 + 49297) % 233280)
            return arg.rndSeed / 233280.0
        }
        seed = (seed * 9301 + 49297) % 233280
        arg.seed = seed
        return seed / 233280.0
    }

}