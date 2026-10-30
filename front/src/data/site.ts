// src/data/site.ts
export const site = {
    name: "Store by aviles",
};

export type NavIcon =
    | "store"
    | "user"
    | "overview"
    | "shop"
    | "about"
    | "signin"
    | "settings"
    | "default";

export interface NavItem {
    href: string;
    label: string;
    icon?: NavIcon;
}

export const navItems: NavItem[] = [
    { href: "/overview/", label: "Inicio", icon: "overview" },
    { href: "/shop/", label: "Comprar", icon: "shop" },
    { href: "/about/", label: "Acerca de nosotros", icon: "about" },
    { href: "/sign-in/", label: "Inicia Sesión", icon: "signin" },
];
