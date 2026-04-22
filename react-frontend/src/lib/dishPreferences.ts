const LIKE_KEY = 'likedDishes';
const DISLIKE_KEY = 'dislikedDishes';
const FAV_KEY = 'favouriteDishes';

function getIds(key: string): number[] {
  try {
    return JSON.parse(localStorage.getItem(key) || '[]');
  } catch {
    return [];
  }
}

function setIds(key: string, ids: number[]) {
  localStorage.setItem(key, JSON.stringify(ids));
}

export function getLikedDishes(): number[] {
  return getIds(LIKE_KEY);
}
export function getDislikedDishes(): number[] {
  return getIds(DISLIKE_KEY);
}
export function getFavouriteDishes(): number[] {
  return getIds(FAV_KEY);
}

export function likeDish(id: number) {
  let liked = getLikedDishes();
  let disliked = getDislikedDishes();
  liked = Array.from(new Set([...liked, id]));
  disliked = disliked.filter(did => did !== id);
  setIds(LIKE_KEY, liked);
  setIds(DISLIKE_KEY, disliked);
}

export function dislikeDish(id: number) {
  let disliked = getDislikedDishes();
  let liked = getLikedDishes();
  disliked = Array.from(new Set([...disliked, id]));
  liked = liked.filter(lid => lid !== id);
  setIds(DISLIKE_KEY, disliked);
  setIds(LIKE_KEY, liked);
}

export function favouriteDish(id: number) {
  let favs = getFavouriteDishes();
  favs = Array.from(new Set([...favs, id]));
  setIds(FAV_KEY, favs);
}

export function unfavouriteDish(id: number) {
  let favs = getFavouriteDishes().filter(fid => fid !== id);
  setIds(FAV_KEY, favs);
}