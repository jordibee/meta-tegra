UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc
require u-boot-tegra-common-${PV}.inc

PROVIDES += "u-boot"
FLASH_TOOLS= "${@'${SOC_FAMILY}' if d.getVar('SOC_FAMILY') != 'tegra194' else 'tegra186'}"
DEPENDS += "dtc-native bc-native ${FLASH_TOOLS}-flashtools-native tegra-bootfiles nv-tegra-release"
CBOOTDEP = "virtual/kernel:do_deploy"
CBOOTDEP_append_tegra186 = " cboot:do_deploy"
CBOOTDEP_append_tegra194 = " cboot:do_deploy"
BUPDEPS = ""
BUPDEPS_tegra186 = "cboot:do_deploy virtual/kernel:do_deploy"
BUPDEPS_tegra194 = "cboot:do_deploy virtual/kernel:do_deploy"

inherit image_types_tegra

UBOOT_BOOTIMG_BOARD ?= "/dev/mmcblk0p1"

uboot_make_bootimg() {
    rm -f ${B}/initrd
    touch ${B}/initrd
    if [ -n "${UBOOT_CONFIG}" ]; then
        unset i j k
	for config in ${UBOOT_MACHINE}; do
	    i=$(expr $i + 1)
	    for type in ${UBOOT_CONFIG}; do
	        j=$(expr $j + 1)
	        if [ $j -eq $i ]; then
	            for binary in ${UBOOT_BINARIES}; do
		        k=$(expr $k + 1)
		        if [ $k -eq $i ]; then
		            f="${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}"
		            rm -f $f
			    ${STAGING_BINDIR_NATIVE}/${FLASH_TOOLS}-flash/mkbootimg \
			        --kernel ${B}/${config}/${binary} --ramdisk ${B}/initrd --cmdline "" \
			        --board "${UBOOT_BOOTIMG_BOARD}" --output $f
			    uboot_bup_payload $f
			fi
		    done
		    unset k
		fi
	    done
	    unset j
	done
	unset i
    else
        mv ${UBOOT_BINARY} ${UBOOT_BINARY}.orig
	${STAGING_BINDIR_NATIVE}/${FLASH_TOOLS}-flash/mkbootimg \
	    --kernel ${UBOOT_BINARY}.orig --ramdisk ${B}/initrd --cmdline "" \
	    --board "${UBOOT_BOOTIMG_BOARD}" --output ${UBOOT_BINARY}
	uboot_bup_payload ${UBOOT_BINARY}
    fi
}

do_compile_append() {
    uboot_make_bootimg
}

uboot_bup_payload() {
    :
}

uboot_bup_payload_base() {
    oe_make_bup_payload ${B}/$1
    cp ${WORKDIR}/bup-payload/bl_update_payload ${1}.bup-payload
}

uboot_bup_payload_tegra186() {
    uboot_bup_payload_base $1
}

uboot_bup_payload_tegra194() {
    uboot_bup_payload_base $1
}

do_compile[depends] += "${BUPDEPS}"

do_deploy_common() {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -d ${DEPLOYDIR}
                    install -m 644 ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}.bup-payload
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}.bup-payload
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}.bup-payload
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}.bup-payload
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}.bup-payload
                fi
            done
            unset  j
        done
        unset  i
    else
        install -d ${DEPLOYDIR}
        install -m 644 ${B}/${UBOOT_BINARY}.bup-payload ${DEPLOYDIR}/${UBOOT_IMAGE}.bup-payload
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY}.bup-payload ${UBOOT_SYMLINK}.bup-payload
        ln -sf ${UBOOT_IMAGE}.bup-payload ${UBOOT_SYMLINK}.bup-payload
        ln -sf ${UBOOT_IMAGE}.bup-payload ${UBOOT_BINARY}.bup-payload
    fi
}

do_deploy_append_tegra186 () {
    do_deploy_common
}

do_deploy_append_tegra194 () {
    do_deploy_common
}

do_install_append() {
    if [ -n "${INITRAMFS_IMAGE}" -a "${TEGRA_INITRAMFS_INITRD}" = "1" ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.cpio.gz ${D}/boot/initrd
    fi
}
do_install[depends] += "${@'${INITRAMFS_IMAGE}:do_image_complete' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('TEGRA_INITRAMFS_INITRD') == '1' else ''}"

RPROVIDES_${PN} += "u-boot"
