(function () {
    'use strict';

    var gulp = require('gulp');
    var utils = require('./gulp.utils');
    var $ = require('gulp-load-plugins')({lazy: true});
    var sass = require('gulp-sass');
    var debug = require('gulp-debug');
    var clean = require('gulp-clean');
    var karma = require('karma').server;

    var client = './client/';
    var server = './src/main/webapp/';

    var jasmine = require('gulp-jasmine');

    var config = {
        jsOrder: [
            '**/jquery*.js',
            '**/angular.js',
            '**/angular*.js',
            '**/app.module.js',
            '**/*.module.js',
            '**/*.js'
        ],
        client : client,
        server : server,
        indexPage : client + 'index.html',
        serverIndexPage: server + 'index.html',
        styles : server + 'styles/',
        sassSrc: client + 'styles/**/*.scss',
        cssSrc: server + 'styles/*.css',
        appSrc: client + 'app/**/*.js',
        htmlSrc: client + 'views/*.html',
        cssOrder: []
    };

    gulp.task('default', ['build']);


    gulp.task('copy-javascript',['clean'],  function() {
    utils.log('Copying javascript files');
        return gulp
        .src(config.appSrc)
        .pipe(debug({title : 'copy-javascript'}))
        .pipe(gulp.dest(config.server +'app/'));
    });

    gulp.task('copy-html', ['copy-javascript'], function() {
        utils.log('Copying html files');
        return gulp
        .src(client + 'views/**/*.html')
        .pipe(gulp.dest(config.server +'views/'));
    });

    gulp.task('copy-assets', ['copy-html'], function() {
        utils.log('Copying html files');
        return gulp
        .src([ client + 'assets/**/*.*'])
        .pipe(debug())
        .pipe(gulp.dest(config.server +'assets/'));
    });

    gulp.task('build',['inject'],function() {
        utils.log('Finalising build');
    });

    /**
     * Wire-up the bower dependencies
     * @return {Stream}
     */
    gulp.task('wiredep', ['styles', 'copy-assets'], function() {
        utils.log('Wiring the bower dependencies into the html');

        var wiredep = require('wiredep');

        return gulp
            .src(wiredep().js)
            .pipe(debug())
            .pipe(gulp.dest(config.server +'app/'));
    });

    /**
     * Remove all files from the webapps folder
     * @param  {Function} done - callback when complete
     */
    gulp.task('clean', function() {
        return gulp
        .src(server +'*')
        .pipe(clean());
    });

    /**
     * Compile sass to css
     * @return {Stream}
     */
    gulp.task('styles',  function() {
        utils.log('Compiling Sass --> CSS');

        return gulp.src(config.sassSrc).pipe(debug())
            .pipe($.plumber())
            .pipe(sass({includePaths:
                ['./node_modules/govuk-elements-sass/public/sass/',
                './node_modules/govuk_frontend_toolkit/stylesheets/',
                './node_modules/govuk_template_ejs/assets/stylesheets/']
                }).on('error', sass.logError))
            .pipe(gulp.dest(config.styles));
    });

    gulp.task('inject', ['wiredep'], function() {
        utils.log('Wire up css into the html, after files are ready');

        return gulp
            .src(config.indexPage)
            .pipe(debug({title:'Injecting into '}))
            .pipe(utils.inject(config.cssSrc))
            .pipe(utils.inject(config.server + 'app/*.js','',config.jsOrder))
            //.pipe(utils.injectMetaTag('meta-tag-version', 'version', pjson.version, false))
            .pipe(gulp.dest(config.server));
    });

    gulp.task('watch', function() {
        gulp.watch([config.sassSrc, config.appSrc, config.htmlSrc, config.indexPage], ['build']);
    });

    gulp.task('test', function (done) {
       karma.start({
         configFile: __dirname + '/karma.conf.js'
       }, done);
    });

})();





