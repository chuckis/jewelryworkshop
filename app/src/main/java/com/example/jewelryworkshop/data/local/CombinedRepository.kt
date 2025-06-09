package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.MetalAlloyRepository
import com.jewelryworkshop.app.domain.repository.TransactionRepository

interface CombinedRepository : TransactionRepository, MetalAlloyRepository