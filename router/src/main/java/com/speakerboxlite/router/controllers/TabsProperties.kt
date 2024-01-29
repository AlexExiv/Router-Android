package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.annotations.TabUnique

data class TabsProperties(val tabRouteInParent: Boolean,
                          val backToFirst: Boolean,
                          val tabUnique: TabUnique)
