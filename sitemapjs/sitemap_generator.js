const SitemapGenerator = require('sitemap-generator');

// create generator
const generator = SitemapGenerator(process.argv[2], {
  //stripQuerystring: false
  lastMod: true
});

// register event listeners
generator.on('done', () => {
  // sitemaps created
});

// start the crawler
generator.start();