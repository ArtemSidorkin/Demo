package com.example.demo;

import javax.validation.Valid;
import java.util.List;

public class CreateDemoObjectRequest
{
	@Valid
	private List<CreateNestedDemoObjectRequest> nestedDemoObjects;

	public CreateDemoObjectRequest(List<CreateNestedDemoObjectRequest> nestedDemoObjects)
	{
		this.nestedDemoObjects = nestedDemoObjects;
	}

	public CreateDemoObjectRequest() {}

	public List<CreateNestedDemoObjectRequest> getNestedDemoObjects()
	{
		return nestedDemoObjects;
	}

	public void setNestedDemoObjects(List<CreateNestedDemoObjectRequest> nestedDemoObjects)
	{
		this.nestedDemoObjects = nestedDemoObjects;
	}
}
