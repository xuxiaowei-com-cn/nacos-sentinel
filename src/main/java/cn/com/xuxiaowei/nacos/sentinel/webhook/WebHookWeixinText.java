package cn.com.xuxiaowei.nacos.sentinel.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
public class WebHookWeixinText {

	@Setter(AccessLevel.NONE)
	private String msgtype = "text";

	@Setter(AccessLevel.NONE)
	private Text text = new Text();

	public WebHookWeixinText(@NonNull String content) {
		this.text.setContent(content);
	}

	public void setMentionedList(List<String> mentionedList) {
		this.text.setMentionedList(mentionedList);
	}

	public void setMentionedMobileList(List<String> mentionedMobileList) {
		this.text.setMentionedMobileList(mentionedMobileList);
	}

	@Data
	public static class Text {

		/**
		 * 文本内容，最长不超过2048个字节，必须是utf8编码
		 */
		private String content;

		/**
		 * userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
		 */
		@JsonProperty("mentioned_list")
		private List<String> mentionedList;

		/**
		 * 手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
		 */
		@JsonProperty("mentioned_mobile_list")
		private List<String> mentionedMobileList;

	}

}
