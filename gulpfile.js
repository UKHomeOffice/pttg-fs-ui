var target = 'src/main/webapp/';
var sourcePath = 'frontend/src/';





var gulp = require('gulp');
var async = require('async');
var run = require('run-sequence');
var minifyHTML = require('gulp-html-minifier');
// var imagemin = require('gulp-imagemin');
// var imageminJpegRecompress = require('imagemin-jpeg-recompress');
var uglify = require('gulp-uglify');
// var nodemon = require('gulp-nodemon');
var templateCache = require('gulp-angular-templatecache');
var del = require('del');
var concat = require('gulp-concat');
var plumber = require('gulp-plumber');
var gutil = require('gulp-util');
var sass = require('gulp-sass');
var autoprefixer = require('gulp-autoprefixer');
var htmlmin = require('gulp-htmlmin');
var sourcemaps = require('gulp-sourcemaps');


// error function for plumber
var onError = function (err) {
  gutil.beep();
  console.log(err);
  this.emit('end');
};

// 'govuk_template': 'frameworks/govuk_template_mustache',
// 'govuk_frontend_toolkit': 'node_modules/govuk_frontend_toolkit',
// 'govuk_elements_sass': 'node_modules/govuk-elements-sass/public/sass'

var config = {
  sass: {
    src:  sourcePath + 'styles/main.scss',
    options: {
      noCache: true,
      compass: false,
      bundleExec: true,
      sourcemap: true,
      outputStyle: 'compressed',
      includePaths: ['node_modules/govuk-elements-sass/public/sass','node_modules/govuk_frontend_toolkit/stylesheets']
    }
  },
  autoprefixer: {
    browsers: [
      'last 2 versions',
      'safari 5',
      'ie 8',
      'ie 9',
      'opera 12.1',
      'ios 6',
      'android 4'
    ],
    cascade: true
  }
};

gulp.task('assets', function () {
  gulp.src([sourcePath + 'assets/**/*']).pipe(gulp.dest(target +'assets'));
});

// gulp.task('clean', function () {
//   return del([
//     target + 'main.js',
//     '_temp/main.js',
//     '_temp/templates.js'
//   ]);
// });


// gulp.task('fonts', function() {
//   return gulp.src(['app/fonts/*/*'])
//     .pipe(inlineFonts({ name: 'fonts' }))
//     .pipe(gulp.dest(target));
// });


gulp.task('sass', function(){
  return gulp.src(config.sass.src)
    .pipe(plumber({ errorHandler: onError }))
    .pipe(autoprefixer(config.autoprefixer.browsers))
    .pipe(sass(config.sass.options)) // Using gulp-sass
    .pipe(gulp.dest(target + 'styles'));
});


// gulp.task('images', function () {
//   return gulp.src('app/images/*')
//     .pipe(imagemin({
//       progressive: true,
//       svgoPlugins: [{cleanupIDs:{remove: false}}],
//       use: [imageminJpegRecompress({loops: 3, min:50, max:80})]
//     }))
//     .pipe(gulp.dest(target + 'images'));
// });


gulp.task('minifyHtml', function() {
  return gulp.src(sourcePath + '*.html')
    .pipe(plumber({ errorHandler: onError }))
    .pipe(htmlmin({collapseWhitespace: true}))
    .pipe(gulp.dest(target))
});


//
gulp.task('uglify', function () {
  return gulp.src([
    sourcePath + 'app/main.js',
    '_temp/templates.js',
    sourcePath + 'app/modules/**/*.js',
    'node_modules/pttg-angular/src/app/modules/forms/forms.js'
    ])
  .pipe(sourcemaps.init())
  .pipe(plumber())
  .pipe(uglify())
  .pipe(concat('main.js'))
  .pipe(sourcemaps.write('./'))
  .pipe(gulp.dest(target + 'app'));
});


gulp.task('angTemplates', function () {
  return gulp.src([
    sourcePath + 'app/modules/**/*.html',
    'node_modules/pttg-angular/src/app/modules/form*/*.html'
  ])
  .pipe(plumber())
  .pipe(htmlmin({collapseWhitespace: true}))
  .pipe(templateCache({root: 'modules/', module:'hod.proving'}))
  .pipe(gulp.dest('_temp'));
});


gulp.task('vendor', function () {
  return gulp.src([
    'node_modules/angular/angular.min.js',
    'node_modules/angular-aria/angular-aria.min.js',
    'node_modules/angular-ui-router/release/angular-ui-router.min.js',
    'node_modules/angular-ui-validate/dist/validate.min.js',
    'node_modules/underscore/underscore-min.js',
    'node_modules/moment/min/moment.min.js',
  ])
  .pipe(plumber())
  .pipe(concat('vendor.js'))
  .pipe(gulp.dest(target + 'app'));
});


gulp.task('templateAndUglify', function () {
  async.series([
    function (done) {
      run(['angTemplates'], function () {
        done();
      });
    },
    function (done) {
      run(['uglify'], function () {
        done();
      });
    },
  ], function () {
    console.log('templateAndUglify done');
  });
});


gulp.task('startwatch', function() {
  gulp.watch(sourcePath + 'index.html', ['minifyHtml']);
  gulp.watch(sourcePath + 'app/modules/**/*.html', ['templateAndUglify']);
  gulp.watch([sourcePath + 'app/main.js', sourcePath + 'app/modules/**/*.js'], ['uglify']);
  gulp.watch(sourcePath + 'styles/*.scss', ['sass']);
});

gulp.task('watch', ['startwatch', 'vendor']);
gulp.task('default', ['assets', 'sass', 'minifyHtml', 'vendor', 'templateAndUglify']);
gulp.task('inline', ['default', 'inlineHTML']);

