package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.MetalAlloyRepository
import com.example.jewelryworkshop.domain.TransactionRepository

interface CombinedRepository : TransactionRepository, MetalAlloyRepository