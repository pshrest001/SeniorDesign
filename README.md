# Senior Design II Advanced Call Blocker App

Overview
---


Features
---
- Managing the Blacklist - to block phone numbers
- Managing the Whitelist - to exclude phone numbers from blocking

Permissions granting
---
Because of the security policy in Android 4.4 and up to block the incoming SMS-messages
it is required to give to the application the permissions of "Default SMS-application" in the Settings.
If necessary, you can always opt-out and return to the previous "Default SMS-application".
In this case, the SMS blocking will be disabled. At the moment the application supports blocking
and exchanging SMS-messages only. Support for MMS-messages has not yet been implemented.

Because of the security policy in Android 6.0 and up, the application requires permissions:
1. "Access photos, media and files". Allows reading and writing files in the internal memory of
the device - for storing the block lists.
2. "Send and view SMS-messages". Allows to display and block incoming SMS-messages.
3. "Make and manage phone calls". Allows to block incoming calls.
4. "Access contacts". Allows to display the list of contacts.

Functionality details
---
1. Messaging.
Messages list displays SMS-chats, sorted by the time of the last message in them. From here you can
go to the reading, writing and sending SMS-messages.

2. Blacklist.
This is the list of contacts with numbers, calls and SMS from which you\'re planning to block.
Add a contact to the list by clicking the \"+\" toolbar button and from the resulting menu choose
one of the suggested ways to add a contact. If you manually add a blocked contact, you can choose
one of the rules for comparing numbers: \"equals\", \"contains\", \"starts with\", or \"ends with\".
To edit, delete, or move a contact to another list, make a long click on its line. If you want to
temporarily disable blocking contacts from the Blacklist, you can do this in the Settings.

3. Whitelist.
This is the list of contacts with numbers, calls and SMS from which will never be blocked. This
list is useful for additional setting of exceptions from blocking. It has a higher priority than
other lists when resolving conflicts between them. Managing the contacts in the Whitelist is
similar to the Blacklist.
