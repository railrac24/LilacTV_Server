package com.badjin.lilactv.controller

import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import java.util.stream.IntStream
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import java.util.*
import kotlin.collections.ArrayList


@Controller
class BookController {

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @GetMapping("/listBooks")
    fun listBooks(
            model: Model,
            @RequestParam("page") page: Optional<Int>,
            @RequestParam("size") size: Optional<Int>): String {
        val currentPage = page.orElse(1)
        val pageSize = size.orElse(5)
        val units = serviceModule.findPaginated(PageRequest.of(currentPage - 1, pageSize))

        model["units"] = units

        val totalPages = units.totalPages
        if (totalPages > 0) {
            val pageNumbers = Array(totalPages) { i -> (i+1).toString() }
//            val pageNumbers = IntStream.rangeClosed(1, totalPages).boxed()
//                    .collect<List<Int>, Any>(Collectors.toList())
            model["pageNumbers"] = pageNumbers
            if (currentPage == 1) model["firstPage"] = true
            if (currentPage == units.number+1) model["active"] = true
            if (currentPage == totalPages) model["lastPage"] = true

            for (i in pageNumbers) print("i = $i,")
            println("total = ${units.totalPages}, pages = $pageNumbers, current = ${units.number}")
        }

        return "listBooks"
    }
}