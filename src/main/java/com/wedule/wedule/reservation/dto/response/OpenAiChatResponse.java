package com.wedule.wedule.reservation.dto.response;

import java.util.List;

// OpenAI API의 응답 전체 구조를 그대로 받기 위한 DTO
// OpenAI가 이런 형태로 응답하기 때문에, 그 구조에 맞춰서 만듦
public class OpenAiChatResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}