SUMMARY = "An initramfs framework extension allowing to update the root squashfs filesystem."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://rootupdate"

RDEPENDS:${PN} += "initramfs-module-rootfs"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${UNPACKDIR}/rootupdate ${D}/init.d/91-rootupdate
}

FILES:${PN} = "/init.d/91-rootupdate"
