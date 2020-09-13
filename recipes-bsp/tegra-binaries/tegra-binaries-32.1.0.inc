LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "https://mirrors.beethedata.com/l4t/32.1/common/JAX-TX2-Jetson_Linux_R32.1.0_aarch64.tbz2"
SRC_MD5SUM = "881fedee06a7446953a064b574227980"
SRC_SHA256SUM = "e7ecaa32928484398d2492e6e9911262b4663e720b4867404d8fe1928fe483aa"
SRC_MD5SUM_tegra210 = "15fda16d83e9a189ea5534108f57af2f"
SRC_SHA256SUM_tegra210 = "e4902a952245217227486322b3e8cef47942f87f2ea91f9e67d700314ae3ade3"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_bsp

SRC_URI += "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
    file://nvpmodel.init \
    file://nvpmodel.service \
    file://nvphs.init \
    file://nvphs.service \
    file://nvs-service.init \
    file://nvs-service.service \
    file://nvstartup.init \
    file://nvstartup.service \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
    file://tegra210-flash-helper.sh \
"

FRANKENSTEIN ?= "yes"
ORIGINAL_S = "${TMPDIR}/work-shared/L4T-native-tegra186-32.1.0-r0/Linux_for_Tegra"
FRANKENSTEIN_S = "${TMPDIR}/work-shared/L4T-${SOC_FAMILY}-32.3.1-r0/Linux_for_Tegra"
S = "${@ d.getVar('FRANKENSTEIN_S') if d.getVar('FRANKENSTEIN') == 'yes' else d.getVar('ORIGINAL_S') }"
B = "${WORKDIR}/build"

do_preconfigure() {
    cp ${WORKDIR}/*-daemon.* ${WORKDIR}/nvstartup.* ${S}/
    cp ${WORKDIR}/nvpmodel.* ${WORKDIR}/nvphs.* ${WORKDIR}/nvs-service.* ${S}/
    cp ${WORKDIR}/tegra*-flash-helper.sh ${S}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "tegra"
COMPATIBLE_MACHINE_tegra124 = "(-)"
