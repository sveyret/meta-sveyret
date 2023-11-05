# This will create a systemd NFS mount service (statically mounting the remote directory) and an automount
# service (mounting the remote directory only if the mountpoint is accessed).
# This is using NFS 4.2 and therefore requires the appropriate options to be set in the kernel.
SYSTEMD_PACKAGES = "${PN} ${PN}-auto"
PACKAGES = "${SYSTEMD_PACKAGES} ${PN}-common"

RDEPENDS:${PN} += "${PN}-common"
RDEPENDS:${PN}-auto += "${PN}-common"
RDEPENDS:${PN}-common += "nfs-utils-mount"

python () {
    remote = d.getVar("NFS_MOUNT_REMOTE")
    mountpoint = d.getVar("NFS_MOUNT_MOUNTPOINT")
    if not remote or not mountpoint:
        bb.error("Missing NFS mount variables")
        return
    d.setVar("NFS_MOUNT_NAME", mountpoint[1:].replace("/", "-"))
}

NFS_MOUNT_DESCRIPTION ??= "NFS remote directory"

# Keep empty to wait for all interfaces to be ready
NFS_WAITING_INTERFACE ?= ""

SYSTEMD_SERVICE:${PN} = "${NFS_MOUNT_NAME}.mount"
SYSTEMD_SERVICE:${PN}-auto = "${NFS_MOUNT_NAME}.automount"

PACKAGESPLITFUNCS =+ "move_common_file"

inherit systemd

do_install:append () {
    install -d ${D}${NFS_MOUNT_MOUNTPOINT}
    install -d ${D}${systemd_system_unitdir}

    wait_interface=
    [ -z "${NFS_WAITING_INTERFACE}" ] || wait_interface="@${NFS_WAITING_INTERFACE}"

    cat <<EOF >${D}${systemd_system_unitdir}/${NFS_MOUNT_NAME}.mount
[Unit]
Description=Mount ${NFS_MOUNT_DESCRIPTION}
DefaultDependencies=no
Requires=systemd-networkd-wait-online$wait_interface.service
After=systemd-networkd-wait-online$wait_interface.service
After=remote-fs-pre.target
Conflicts=umount.target
Before=umount.target
Before=remote-fs.target

[Mount]
What=${NFS_MOUNT_REMOTE}
Where=${NFS_MOUNT_MOUNTPOINT}
Type=nfs
Options=ro,vers=4.2

[Install]
WantedBy=multi-user.target
EOF
    chmod 0644 ${D}${systemd_system_unitdir}/${NFS_MOUNT_NAME}.mount

    cat <<EOF >${D}${systemd_system_unitdir}/${NFS_MOUNT_NAME}.automount
[Unit]
Description=Automount ${NFS_MOUNT_DESCRIPTION}

[Automount]
Where=${NFS_MOUNT_MOUNTPOINT}

[Install]
WantedBy=multi-user.target
EOF
    chmod 0644 ${D}${systemd_system_unitdir}/${NFS_MOUNT_NAME}.automount
}

python move_common_file() {
    pn_file_name = "FILES:" + d.getVar('PN')
    common_file_name = pn_file_name + "-common"
    pn_files = d.getVar(pn_file_name, False) or ""
    service_file = oe.path.join(d.getVar("systemd_system_unitdir"), d.getVar("NFS_MOUNT_NAME") + ".mount")
    d.setVar(pn_file_name, "")
    for file_append in pn_files.split():
        if file_append == service_file:
            d.appendVar(common_file_name, " " + file_append)
        else:
            d.appendVar(pn_file_name, " " + file_append)
}

FILES:${PN}-common = "${NFS_MOUNT_MOUNTPOINT}"
