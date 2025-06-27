package com.example.jewelryworkshop.domain

class Reports(
    private val reports: List<Report>,
) {
    fun size(): Int {
        return reports.size
    }

    fun isEmpty(): Boolean {
        return reports.isEmpty()
    }

    fun all(): List<Report> {
        return reports
    }
}