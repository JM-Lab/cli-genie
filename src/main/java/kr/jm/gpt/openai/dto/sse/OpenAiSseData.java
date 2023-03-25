package kr.jm.gpt.openai.dto.sse;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiSseData{
	private Long created;
	private String model;
	private String id;
	private List<ChoicesItem> choices;
	private String object;
}