package ru.jmorozov.prodkalendar.service.command

interface SiteParsingService {
    fun getDownloadHref(url: String, cssQuery: String): String
}