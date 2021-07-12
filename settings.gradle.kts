rootProject.name = "doctor"

include("doctor-protocol")
include("doctor-core")
include("doctor-network")
include("doctor-client")

include("doctor-plugin:doctor-plugin-forge-core")

include("doctor-plugin:doctor-plugin-forge-laggoggles")

include("doctor-translate:doctor-translate-core")

include("doctor-translate:doctor-translate-mc112")
include("doctor-translate:doctor-translate-mc112-vanilla")
include("doctor-translate:doctor-translate-mc112-mod")

include("doctor-translate:doctor-translate-mc116")
include("doctor-translate:doctor-translate-mc116-vanilla")

include("doctor-all")
include("doctor-plugin:doctor-plugin-forge-all")
include("doctor-translate:doctor-translate-mc116-all")
include("doctor-translate:doctor-translate-mc112-all")
include("doctor-translate:doctor-translate-all")
include("doctor-plugin:doctor-plugin-forge-astralsorcery")
findProject(":doctor-plugin:doctor-plugin-forge-astralsorcery")?.name = "doctor-plugin-forge-astralsorcery"
include("doctor-plugin:doctor-plugin-forge-astralsorcery")
findProject(":doctor-plugin:doctor-plugin-forge-astralsorcery")?.name = "doctor-plugin-forge-astralsorcery"
