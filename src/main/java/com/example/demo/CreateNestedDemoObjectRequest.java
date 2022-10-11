package com.example.demo;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

public class CreateNestedDemoObjectRequest
{
	@Valid @Pattern(regexp = "^[\\w]+$")
	private String data;

	public CreateNestedDemoObjectRequest(String data)
	{
		this.data = data;
	}

	public CreateNestedDemoObjectRequest() {}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}
}
