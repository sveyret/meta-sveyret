SUMMARY = "Temurin JDK Binaries"

JAVA_TYPE = "jdk"
JAVA_SHA256:x86-64 = "974d3acef0b7193f541acb61b76e81670890551366625d4f6ca01b91ac152ce0"

require temurin.inc

S = "${WORKDIR}/jdk-${PV}"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "java javac keytool"

ALTERNATIVE_LINK_NAME[java] = "${bindir}/java"
ALTERNATIVE_TARGET[java] = "${JAVA_HOME}/bin/java"

ALTERNATIVE_LINK_NAME[javac] = "${bindir}/javac"
ALTERNATIVE_TARGET[javac] = "${JAVA_HOME}/bin/javac"

ALTERNATIVE_LINK_NAME[keytool] = "${bindir}/keytool"
ALTERNATIVE_TARGET[keytool] = "${JAVA_HOME}/bin/keytool"
