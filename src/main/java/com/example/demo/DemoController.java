package com.example.demo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class DemoController
{
	@RequestMapping(method = RequestMethod.POST, path = "/api/test")
	@ResponseBody
	public void creteDemoObject(@RequestBody @Validated CreateDemoObjectRequest createDemoObjectRequest)
	{
		System.out.println("Demo object: " + createDemoObjectRequest + " created");
	}
}
