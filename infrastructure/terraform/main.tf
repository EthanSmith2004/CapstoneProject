terraform {
  required_providers {
    proxmox = {
      source = "telmate/proxmox"
      version = "3.0.1-rc8"
    }
  }
}

provider "proxmox" {
  pm_api_url = "https://constantinople.superuserdone.com:8006/api2/json"
}

locals {
  ssh_key_path = "${path.module}/../keys/louis.pub"
}

resource "proxmox_lxc" "postgres" {
  target_node  = "pve"
  hostname     = "postgres"
  ostemplate   = "local:vztmpl/debian-12-standard_12.7-1_amd64.tar.zst"
  unprivileged = true

  ssh_public_keys = file(local.ssh_key_path)

  features {
    nesting = true
  }

  rootfs {
    storage = "local-lvm"
    size    = "8G"
  }

  network {
    name   = "eth0"
    bridge = "vmbr0"
    ip     = "10.70.70.10/24"
    gw     = "192.168.1.1"
  }

  onboot = true
  start  = true
}

resource "proxmox_lxc" "api_dev" {
  target_node  = "pve"
  hostname     = "api-dev"
  ostemplate   = "local:vztmpl/debian-12-standard_12.7-1_amd64.tar.zst"
  unprivileged = true

  ssh_public_keys = file(local.ssh_key_path)
  
  features {
    nesting = true
  }

  rootfs {
    storage = "local-lvm"
    size    = "8G"
  }

  network {
    name   = "eth0"
    bridge = "vmbr0"
    ip     = "10.70.70.11/24"
    gw     = "192.168.1.1"
  }

  onboot = true
  start  = true
}

resource "proxmox_lxc" "api_prod" {
  target_node  = "pve"
  hostname     = "api-prod"
  ostemplate   = "local:vztmpl/debian-12-standard_12.7-1_amd64.tar.zst"
  unprivileged = true

  ssh_public_keys = file(local.ssh_key_path)
  
  features {
    nesting = true
  }

  rootfs {
    storage = "local-lvm"
    size    = "8G"
  }

  network {
    name   = "eth0"
    bridge = "vmbr0"
    ip     = "10.70.70.12/24"
    gw     = "192.168.1.1"
  }

  onboot = true
  start  = true
}

resource "proxmox_lxc" "nginx" {
  target_node  = "pve"
  hostname     = "nginx"
  ostemplate   = "local:vztmpl/debian-12-standard_12.7-1_amd64.tar.zst"
  unprivileged = true

  ssh_public_keys = file(local.ssh_key_path)
  
  features {
    nesting = true
  }

  rootfs {
    storage = "local-lvm"
    size    = "8G"
  }

  network {
    name   = "eth0"
    bridge = "vmbr0"
    ip     = "10.70.70.13/24"
    gw     = "192.168.1.1"
  }

  onboot = true
  start  = true
}
