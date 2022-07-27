package my.example.plugs

import my.example.annotation.SendAuto
import my.example.utils.CacheMap
import my.miraiplus.annotation.EventHandle
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.PlainText

/**
 *  @Date:2022/1/8
 *  @author bin
 *  @version 1.0.0
 */
object CQBotRepeat {

	@JvmStatic
	private val cache = CacheMap<Long, RepeatCache>()

	@EventHandle("(复读)", priority = EventPriority.LOWEST)
	@SendAuto
	private fun invoke(event: GroupMessageEvent): String? {
		val node = cache[event.group.id]
		if (node === null || node.msg != event.message.contentToString()) {
			cache[event.group.id] = RepeatCache(event.message.contentToString()).also {
				it += event.sender.id
			}
			return null
		}
		node += event.sender.id
		if (node.size != 4) return ""
		val msg: String = event.message.filterIsInstance<PlainText>().joinToString("") {
			it.contentToString()
		}
		if (msg.length < 4) return msg
		return msg.shuffle()
	}

	@JvmStatic
	private fun String.shuffle(): String {
		return String(toCharArray().apply { shuffle() })
	}

	private class RepeatCache(val msg: String) {
		val users = mutableSetOf<Long>()
		val size get() = users.size

		operator fun plusAssign(qq: Long) {
			users += qq
		}
	}
}
