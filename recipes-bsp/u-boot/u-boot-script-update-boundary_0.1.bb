LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
DEPENDS = "u-boot-mkimage-native"

SRC_URI = " \
	file://bootscript-update.txt \
	file://Licenses/README \
"

S = "${WORKDIR}"

inherit deploy

BOOTSCRIPT ??= "${S}/bootscript-update.txt"

do_mkimage () {
    # allow deploy to use the ${MACHINE} name to simplify things
    if [ ! -d ${S}/${MACHINE} ]; then
        mkdir ${S}/${MACHINE}
    fi

    uboot-mkimage -A arm -O linux -T script -C none -a 0 -e 0 \
                  -n "boot script for update" -d ${BOOTSCRIPT} \
                  ${S}/${MACHINE}/boot-update.scr
}

addtask mkimage after do_compile before do_install

do_compile[noexec] = "1"

# Add compatibility with pre-2017.07 Boundary U-Boot releases
# TODO: remove when nobody is using old U-Boot releases anymore
do_deploy[depends] += "u-boot-script-update-boundary-legacy:do_deploy"

do_install () {
    install -D -m 644 ${S}/${MACHINE}/boot-update.scr ${D}/boot-update.scr
}

do_deploy () {
    install -D -m 644 ${D}/boot-update.scr ${DEPLOYDIR}/boot-update.scr-${MACHINE}-${PV}-${PR}

    cd ${DEPLOYDIR}
    rm -f boot-update.scr-${MACHINE}
    ln -sf boot-update.scr-${MACHINE}-${PV}-${PR} boot-update.scr-${MACHINE}
}

addtask deploy after do_install before do_build

FILES_${PN} += "/"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "(nitrogen6x|nitrogen6x-lite|nitrogen6sx|nitrogen7)"
