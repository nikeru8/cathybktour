package com.daniel.cathybktour.model

data class NewsModel(
    val `data`: MutableList<NewsData> = mutableListOf(),
    val total: Int = 0,
)

data class NewsData(
    val begin: String? = "",
    val description: String = "",
    val end: String? = "",
    val files: MutableList<File> = mutableListOf(),
    val id: Int = 0,
    val links: MutableList<Link> = mutableListOf(),
    val modified: String = "",
    val posted: String = "",
    val title: String = "",
    val url: String = "",
)

data class File(
    val ext: String = "",
    val src: String = "",
    val subject: String = "",
)

data class Link(
    val src: String = "",
    val subject: String = "",
)