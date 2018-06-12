var target = 'public/'
var sourcePath = 'src/'

var gulp = require('gulp')
var async = require('async')
var run = require('run-sequence')
var fs = require('fs')
var uglify = require('gulp-uglify')
var templateCache = require('gulp-angular-templatecache')
var concat = require('gulp-concat')
var plumber = require('gulp-plumber')
var gutil = require('gulp-util')
var htmlmin = require('gulp-htmlmin')
var sourcemaps = require('gulp-sourcemaps')
var sassjs = require('sass')

// error function for plumber
var onError = function (err) {
  gutil.beep()
  console.log(err)
  this.emit('end')
}

var config = {
  sass: {
    src: sourcePath + 'styles/main.scss',
    options: {
      noCache: true,
      compass: false,
      bundleExec: true,
      sourcemap: true,
      outputStyle: 'compressed',
      includePaths: ['node_modules/govuk-elements-sass/public/sass', 'node_modules/govuk_frontend_toolkit/stylesheets']
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
}

gulp.task('assets', function () {
  gulp.src([sourcePath + 'assets/**/*']).pipe(gulp.dest(target + 'assets'))
})

gulp.task('sassjs', function () {
  sassjs.render({
    file: config.sass.src,
    includePaths: ['node_modules/govuk-elements-sass/public/sass',
      'node_modules/govuk_frontend_toolkit/stylesheets',
      'node_modules/chartist/dist/scss'
    ],
    outFile: target + 'styles',
    outputStyle: 'compressed'
  }, function (err, result) {
    if (!err) {
      // No errors during the compilation, write this result on the disk
      fs.writeFile(target + 'styles/main.css', result.css, function (err) {
        if (!err) {
          // file written on disk
        }
      })
    }
  })
})

gulp.task('minifyHtml', function () {
  return gulp.src(sourcePath + '*.html')
    .pipe(plumber({ errorHandler: onError }))
    .pipe(htmlmin({collapseWhitespace: true}))
    .pipe(gulp.dest(target))
})

gulp.task('uglify', function () {
  return gulp.src([
    sourcePath + 'app/main.js',
    '_temp/templates.js',
    sourcePath + 'app/modules/**/*.js'
    // 'node_modules/pttg-angular/src/app/modules/forms/forms.js'
  ])
  .pipe(sourcemaps.init())
  .pipe(plumber())
  .pipe(uglify())
  .pipe(concat('main.js'))
  .pipe(sourcemaps.write('./'))
  .pipe(gulp.dest(target + 'app'))
})

gulp.task('angTemplates', function () {
  return gulp.src([
    sourcePath + 'app/modules/**/*.html'
    // 'node_modules/pttg-angular/src/app/modules/form*/*.html'
  ])
  .pipe(plumber())
  .pipe(htmlmin({collapseWhitespace: true}))
  .pipe(templateCache({root: 'modules/', module: 'hod.proving'}))
  .pipe(gulp.dest('_temp'))
})

gulp.task('vendor', function () {
  return gulp.src([
    'node_modules/angular/angular.min.js',
    'node_modules/angular-aria/angular-aria.min.js',
    'node_modules/angular-ui-router/release/angular-ui-router.min.js',
    'node_modules/angular-ui-validate/dist/validate.min.js',
    'node_modules/underscore/underscore-min.js',
    'node_modules/moment/min/moment.min.js',
    'node_modules/clipboard/dist/clipboard.min.js'
    // 'node_modules/govuk_frontend_toolkit/javascripts/govuk/selection-buttons.js'
  ])
  .pipe(plumber())
  .pipe(concat('vendor.js'))
  .pipe(gulp.dest(target + 'app'))
})

gulp.task('templateAndUglify', function () {
  async.series([
    function (done) {
      run(['angTemplates'], function () {
        done()
      })
    },
    function (done) {
      run(['uglify'], function () {
        done()
      })
    }
  ], function () {
    console.log('templateAndUglify done')
  })
})

gulp.task('startwatch', function () {
  var nodemon = require('gulp-nodemon')

  nodemon({
    script: 'server.js',
    ext: 'js',
    env: { 'NODE_ENV': 'development' },
    cwd: __dirname,
    ignore: ['node_modules/**'],
    watch: ['server.js']
  })
  gulp.watch(sourcePath + 'index.html', ['minifyHtml'])
  gulp.watch(sourcePath + 'app/modules/**/*.html', ['templateAndUglify'])
  gulp.watch([sourcePath + 'app/main.js', sourcePath + 'app/modules/**/*.js'], ['uglify'])
  gulp.watch(sourcePath + 'styles/*.scss', ['sassjs'])
})

gulp.task('test', function (done) {
  var karma = require('karma')
  var server = new karma.Server({ configFile: __dirname + '/karma.conf.js' }, done)
  server.start()
})

gulp.task('build', ['assets', 'sassjs', 'minifyHtml', 'vendor', 'templateAndUglify'])
gulp.task('watch', ['startwatch', 'vendor'])
gulp.task('default', ['build'])
gulp.task('inline', ['default', 'inlineHTML'])
