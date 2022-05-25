const SitemapGenerator = require('sitemap-generator');

// create generator
const generator = SitemapGenerator(process.argv[2], {
  //stripQuerystring: false
  filepath: './sitemap_' + process.argv[3] + '.xml',
  lastMod: true
});

// register event listeners
generator.on('done', () => {
  // sitemaps created
});

// start the crawler
generator.start();