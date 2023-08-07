/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import { RuleTester } from 'eslint';
import { eslintRules } from '@sonar/jsts/rules/core';
import { decoratePreferSpread } from '@sonar/jsts/rules/decorators/prefer-spread-decorator';

const rule = decoratePreferSpread(eslintRules['prefer-spread']);
const ruleTester = new RuleTester({
  parserOptions: { ecmaVersion: 2015 },
});

ruleTester.run(`Spread syntax should be used instead of apply()`, rule, {
  valid: [
    {
      code: `foo.apply(obj, args);`,
    },
  ],
  invalid: [
    {
      code: `foo.apply(null, args);`,
      errors: [
        {
          suggestions: [
            {
              desc: 'Replace apply() with spread syntax',
              output: `foo(...args);`,
            },
          ],
        },
      ],
    },
    {
      code: `obj.foo.apply(obj, args);`,
      errors: [
        {
          suggestions: [
            {
              desc: 'Replace apply() with spread syntax',
              output: `obj.foo(...args);`,
            },
          ],
        },
      ],
    },
  ],
});