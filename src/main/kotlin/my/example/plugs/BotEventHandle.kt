package my.example.plugs

import my.example.annotation.Helper
import my.example.annotation.SendAdmin
import my.example.annotation.SendGroup
import my.example.utils.createLogger
import my.miraiplus.annotation.EventHandle
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.MiraiExperimentalApi

/**
 *  @Date:2022/5/31
 *  @author bin
 *  @version 1.0.0
 */
object BotEventHandle {
	private val logger = createLogger<BotEventHandle>()

	@EventHandle("Bot 上线事件")
	private fun BotOnlineEvent.run() {
		logger.info("${bot.nameCardOrNick} 已上线")
	}

	@EventHandle("Bot 下线事件")
	private fun BotOfflineEvent.run() {
		val msg = "${bot.nameCardOrNick} 已下线, 是否重连: ${reconnect}, 原因: ${
			when (this) {
				is BotOfflineEvent.Active -> "主动离线, 错误: ${cause}"
				is BotOfflineEvent.Force -> "被挤下线, title: ${title}, message: ${message}"
				// is BotOfflineEvent.MsfOffline -> "主动离线, 错误: ${cause}"
				is BotOfflineEvent.Dropped -> "因网络问题而掉线, 错误: ${cause}"
				// is BotOfflineEvent.RequireReconnect -> "主动离线, 错误: ${cause}"
				else -> toString()
			}
		}"
		logger.info(msg)
	}

	@EventHandle("Bot 重新登录事件")
	private fun BotReloginEvent.run() {
		logger.info("${bot.nameCardOrNick} 已重新登录")
	}

	@EventHandle("添加好友事件")
	@SendAdmin
	private fun NewFriendRequestEvent.run(): String {
		val msg = "${fromNick}（${fromId}）来自群 ${fromGroup?.name ?: ""}（${fromGroupId}）请求添加好友消息：\n${message}"
		logger.info("NewFriendRequestEvent: ${msg}")
		return msg
	}

	@EventHandle("邀请加入群事件")
	@SendAdmin
	private fun BotInvitedJoinGroupRequestEvent.run(): String {
		val msg = "${invitorNick}（${invitorId}）邀请加入群 ${groupName}（${groupId}）"
		logger.info("BotInvitedJoinGroupRequestEvent: ${msg}")
		return msg
	}

	@EventHandle("成员加入群事件")
	@Helper("成员加入群事件")
	@SendGroup
	private fun MemberJoinEvent.run(): String {
		val msg = "@${member.nick} ${
			when (this) {
				is MemberJoinEvent.Invite -> "被 @${invitor.nick} 邀请"
				is MemberJoinEvent.Active -> "欢迎"
				is MemberJoinEvent.Retrieve -> "恢复群主身份"
				else -> ""
			}
		}入群"
		logger.info("MemberJoinEvent: ${msg}")
		return msg
	}

	@OptIn(MiraiExperimentalApi::class)
	@EventHandle("Bot 加入群事件")
	@SendAdmin
	private fun BotJoinGroupEvent.run(): String {
		val msg = "bot成功加入群： ${group.name}(${groupId}), 来源：${
			when (this) {
				is BotJoinGroupEvent.Invite -> "邀请人： ${invitor.nick}(${invitor.id})"
				is BotJoinGroupEvent.Active -> "不确定"
				is BotJoinGroupEvent.Retrieve -> "恢复群主身份"
				else -> ""
			}
		}"
		logger.info("BotJoinGroupEvent: ${msg}")
		return msg
	}

	@EventHandle("成员离开群事件")
	@Helper("成员离开群事件")
	@SendGroup
	private fun MemberLeaveEvent.run(): String {
		val msg = "@${member.nick}(${member.id})${
			when (this) {
				is MemberLeaveEvent.Quit -> "主动离开本群"
				is MemberLeaveEvent.Kick -> "被 管理员(@${operator?.nick ?: bot.nick}) 踢出本群"
				else -> "未知方式离开本群"
			}
		}"
		logger.info("MemberLeaveEvent: ${msg}")
		return msg
	}

	@OptIn(MiraiExperimentalApi::class)
	@EventHandle("Bot 离开群事件")
	@SendAdmin
	private fun BotLeaveEvent.run(): String {
		val msg = "bot被踢出群：${groupId}(${group.name}), 原因：${
			when (this) {
				is BotLeaveEvent.Active -> "主动退出, 有被踢出可能"
				is BotLeaveEvent.Kick -> "被踢出群"
				is BotLeaveEvent.Disband -> "群主解散群聊"
				else -> ""
			}
		}"
		logger.info("BotLeaveEvent: ${msg}")
		return msg
	}

	@EventHandle("其他客户端上线事件")
	@SendAdmin
	private fun OtherClientOnlineEvent.run(): String {
		val msg = """其他客户端上线
			|设备名称:${client.info.deviceName}
			|设备类型:${client.info.deviceKind}
		""".trimMargin()
		logger.info("OtherClientOnlineEvent: ${msg}")
		return msg
	}

	@EventHandle("其他客户端上线事件")
	@SendAdmin
	private fun OtherClientOfflineEvent.run(): String {
		val msg = """其他客户端下线
			|设备名称:${client.info.deviceName}
			|设备类型:${client.info.deviceKind}
		""".trimMargin()
		logger.info("OtherClientOnlineEvent: ${msg}")
		return msg
	}

	@EventHandle("Bot 被禁言事件")
	private fun BotMuteEvent.run() {
		val msg = "bot被禁言，群: ${group.name}(${groupId}), 操作人: ${operator.nameCardOrNick}(${operator.id})"
		logger.info("BotMuteEvent: ${msg}")
	}

	@EventHandle("Bot 被取消禁言事件")
	private fun BotUnmuteEvent.run() {
		val msg = "bot被取消禁言，群: ${group.name}(${groupId}), 操作人: ${operator.nameCardOrNick}(${operator.id})"
		logger.info("BotUnmuteEvent: ${msg}")
	}

}
