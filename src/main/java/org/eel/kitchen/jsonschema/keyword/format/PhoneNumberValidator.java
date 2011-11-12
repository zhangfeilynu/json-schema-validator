/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.format;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Attempt to validate the "phone" format specification
 *
 * <p>The draft says the phone MAY match E.123. Quite vague. Here we use
 * Google's <a href="http://code.google.com/p/libphonenumber/">libphonenumber</a>
 * as it is a library specialized in phone number recognition.</p>
 *
 * <p>It will only chek if this is a potential phone number,
 * not whether it is actually valid for your country! If you really want
 * that, you will probably want to write your own {@link KeywordValidator}.</p>
 *
 * @see JsonValidator#registerValidator(String, Class, Class, NodeType...)
 */
//TODO: more tests?
public final class PhoneNumberValidator
    extends AbstractFormatValidator
{
    private static final PhoneNumberUtil parser = PhoneNumberUtil.getInstance();

    public PhoneNumberValidator(final ValidationReport report,
        final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        final String input = node.getTextValue();

        /*
         * The libphonenumber API doc says that no matter what region you put
         * when validating national phone numbers, the number is not actually
         * considered valid for a specific country without further
         * verifications. International phone numbers MUST start with a
         * "+" however, this is a constant.
         *
         * So, this is the only switching point: if it starts with a "+",
         * check with the "no zone" specification, otherwise check with any
         * country code.
         */
        try {
            if (input.startsWith("+"))
                parser.parse(input, "ZZ");
            else
                parser.parse(input, "FR");
        } catch (NumberParseException ignored) {
            report.addMessage("string is not a recognized phone number");
        }

        return report;
    }
}
