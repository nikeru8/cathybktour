package com.daniel.cathybktour.model

data class NewsModel(
    val `data`: MutableList<Data> = mutableListOf(),
    val total: Int = 0,
)

data class Data(
    val begin: Any? = null,
    val description: String = "",
    val end: Any? = null,
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