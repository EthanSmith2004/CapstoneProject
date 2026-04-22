export function getUserPreferences() {
  return {
    hideAllergyDishes: localStorage.getItem('pref_hideAllergyDishes') === 'true',
    hideDislikedDishes: localStorage.getItem('pref_hideDislikedDishes') === 'true',
    sortByLikedDishes: localStorage.getItem('pref_sortByLikedDishes') === 'true',
    favTabDefault: localStorage.getItem('pref_favTabDefault') === 'true',
  };
}