# Settings common to all Xavier (tegra194) SoCs

SOC_FAMILY = "tegra194"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image u-boot"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-configs-nvstartup tegra-configs-udev kernel-module-nvgpu kernel-module-nvs"

INITRAMFS_IMAGE ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'tegra-minimal-initramfs'}"
TEGRA_INITRAMFS_INITRD ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '1'}"
INITRD_IMAGE ?= "${@'tegra-minimal-initramfs' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
INITRD_FSTYPES ?= "${@'cpio.gz.cboot cpio.gz.cboot.bup-payload' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${INITRAMFS_FSTYPES}'}"
IMAGE_CLASSES_append = "${@' image_types_cboot' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
IMAGE_UBOOT ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot'}"

TEGRA_AUDIO_DEVICE ?= "tegrahdat194ref"

SERIAL_CONSOLE = "115200 ttyTCU0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

NVIDIA_CHIP = "0x19"

TEGRA_CHIPREV ?= "2"
TEGRA_FAB ?= "400"
TEGRA_BOARDID ?= "2888"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "10.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_72 --gpu-code=sm_72"

PREFERRED_PROVIDER_libdrm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_drm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"
PREFERRED_PROVIDER_cboot ?= "cboot-prebuilt"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "jax-tx2"
L4T_BSP_BASEPFX = "JAX-TX2"
L4T_BSP_PREFIX = "${L4T_BSP_BASEPFX}-Jetson"