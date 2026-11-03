/**
 * Persistent client state (Cart, Wishlist, Popup flag) stored in localStorage
 * with reactive subscriptions, compatible with Noble Muse theme components.
 */

type Listener<T> = (val: T) => void;

function createPersistentStore<T>(storageKey: string, initialValue: T) {
  let value: T = initialValue;
  const listeners = new Set<Listener<T>>();

  if (typeof window !== 'undefined') {
    try {
      const raw = window.localStorage.getItem(storageKey);
      if (raw !== null) {
        value = JSON.parse(raw);
      }
    } catch {
      value = initialValue;
    }

    window.addEventListener('storage', (e) => {
      if (e.key === storageKey && e.newValue !== null) {
        try {
          value = JSON.parse(e.newValue);
          listeners.forEach((fn) => fn(value));
        } catch {
          // ignore parse errors
        }
      }
    });
  }

  return {
    get(): T {
      return value;
    },
    set(next: T) {
      value = next;
      if (typeof window !== 'undefined') {
        try {
          window.localStorage.setItem(storageKey, JSON.stringify(next));
        } catch {
          // ignore quota errors
        }
      }
      listeners.forEach((fn) => fn(value));
    },
    subscribe(fn: Listener<T>) {
      listeners.add(fn);
      fn(value);
      return () => listeners.delete(fn);
    },
  };
}

export interface CartLine {
  key: string; // handle|size|color
  handle: string;
  size: string;
  color: string;
  quantity: number;
}

export const cart = createPersistentStore<CartLine[]>('njx-muse-cart', []);

export function addToCart(handle: string, size: string, color: string, quantity = 1) {
  const key = `${handle}|${size}|${color}`;
  const lines = [...cart.get()];
  const ex = lines.find((l) => l.key === key);
  if (ex) ex.quantity += quantity;
  else lines.push({ key, handle, size, color, quantity });
  cart.set(lines);
}

export function setCartQty(key: string, quantity: number) {
  cart.set(
    cart
      .get()
      .map((l) => (l.key === key ? { ...l, quantity } : l))
      .filter((l) => l.quantity > 0)
  );
}

export const wishlist = createPersistentStore<string[]>('njx-muse-wishlist', []);

export function toggleWishlist(handle: string): boolean {
  const list = wishlist.get();
  const added = !list.includes(handle);
  wishlist.set(added ? [...list, handle] : list.filter((h) => h !== handle));
  return added;
}

export const popupSeen = createPersistentStore<string>('njx-muse-popup-seen', '');

