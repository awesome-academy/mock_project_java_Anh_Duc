// Auto-generate slug from name
document.getElementById('name').addEventListener('input', function () {
  const slug = slugify(this.value);
  document.getElementById('slug').value = slug;
});

function slugify(str) {
  str = str.replace(/^\s+|\s+$/g, ''); // trim leading/trailing white space
  str = str.toLowerCase(); // convert string to lowercase

  // Remove Vietnamese accents
  str = str.normalize('NFD').replace(/[\u0300-\u036f]/g, ''); // remove combining diacritical marks
  str = str.replace(/đ/g, 'd').replace(/Đ/g, 'd'); // replace đ and Đ

  str = str.replace(/[^a-z0-9 -]/g, '') // remove any non-alphanumeric characters
    .replace(/\s+/g, '-') // replace spaces with hyphens
    .replace(/-+/g, '-'); // remove consecutive hyphens
  return str;
}
