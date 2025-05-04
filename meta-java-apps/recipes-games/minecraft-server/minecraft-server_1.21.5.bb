# Install the minecraft server
# Unit file was taken from https://gitweb.gentoo.org/repo/gentoo.git/tree/games-server/minecraft-server/files/minecraft-server.service
SUMMARY = "Server for Mojang Minecraft Java Edition"
HOMEPAGE = "https://www.minecraft.net/"

LICENSE = "CLOSED"
LICENSE_FLAGS = "commercial"

SERVER_FILE_NAME = "${PN}-${PV}.jar"
SRC_URI = "\
    https://piston-data.mojang.com/v1/objects/e6ec2f64e6080b9b5d9b471b291c33cc7f509733/server.jar;downloadfilename=${SERVER_FILE_NAME};unpack=0 \
    file://systemd.service \
"
SRC_URI[md5sum] = "a325be7531b7a99c985082908a79d966"

RDEPENDS:${PN} += "java2-runtime"

inherit bin_package useradd systemd

INSANE_SKIP:${PN} += "already-stripped"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r minecraft"
USERADD_PARAM:${PN} = "-r -s /sbin/nologin -d ${localstatedir}/lib/${BPN} -g minecraft minecraft"

MINECRAFT_WORLDS ??= "world"
SYSTEMD_SERVICE:${PN} = "${@" ".join(["${BPN}@" + w + ".service" for w in d.getVar('MINECRAFT_WORLDS').split()])}"

S = "${UNPACKDIR}"

do_install() {
    # JAR file
    install -d -m0755 ${D}/opt/${BPN}
    install -m 0644 ${UNPACKDIR}/${SERVER_FILE_NAME} ${D}/opt/${BPN}/

    # Binary
    install -d ${D}${bindir}
    cat <<EOF >${D}${bindir}/${BPN}
#!/bin/sh
java -Xmx1024M -Xms1024M -jar /opt/${BPN}/${SERVER_FILE_NAME} nogui
EOF
    chmod a+x ${D}${bindir}/${BPN}

    # Home directory
    install -d -m0755 ${D}${localstatedir}/lib/${BPN}
    chown -R minecraft:minecraft ${D}${localstatedir}/lib/${BPN}

    # Service
    install -d ${D}/${systemd_system_unitdir}
    sed \
        -e "s,@HOME_DIR@,${localstatedir}/lib/${BPN}," \
        -e "s,@EXEC_PATH@,${bindir}/${BPN}," \
        ${UNPACKDIR}/systemd.service >${D}${systemd_system_unitdir}/${BPN}@.service
    chmod 644 ${D}${systemd_system_unitdir}/${BPN}@.service
}
