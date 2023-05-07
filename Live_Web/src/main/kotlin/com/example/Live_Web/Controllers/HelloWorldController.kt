package com.example.Live_Web.Controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import  org.springframework.web.bind.annotation.RestController


/**
 * Controller mapped under /api/HelloWorld.
 * Returns "Hello World"
 */
@RestController
@RequestMapping("api/hello")
class HelloWorldController {

    @GetMapping
    fun helloWorld() : String = "Hello world"
}