/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var path = require('path');

var jsDir = path.resolve(__dirname, 'src/main/javascript');

module.exports = {
    entry: {
        platon: path.resolve(jsDir, 'platon.js')
    },
    output: {
        path: path.resolve(__dirname, 'src/main/webapp/js'),
        filename: '[name].js'
    },
    module: {
        loaders: [
            {test: /\.html$/, loader: 'vue-template-compiler'},
            {test: /\.css$/, loader: 'style!css'}
        ]
    },
    devtool: 'source-map'
};
