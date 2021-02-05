# Project 1 Instructions
For Project 1, you will be expected to have a frontend in HTML/CSS/JavaScript and a
backend using servlets that connects to your database. You should be able to reuse your
DAOs and database connection, but much of your console logic will no longer be relevant. Think
of logic that manages which submenu users have entered, and logic that captures user input;
this is the kind of logic that will be made obsolete.

On the frontend side, you have some options:

- You may have your servlet return HTML that has been baked into your servlets . In
this way, your servlets would allow for behavior normally achieved by JavaScript, which
may be easier for you.
- You can use JSPs to achieve the above, though you will have to research this.
- You may also have a separate frontend and backend , using your backend only for
information retrieval and your frontend would take care of presentation and dynamic
rearranging of the DOM. This is the best use-case for a REST API.
On the backend side, you have also some options:
- You may use vanilla servlets , optionally in conjunction with some data pattern like Front
Controller or MVC, to deliver information between your database and your frontend.
- You may also use Jersey to simplify the construction of your backend. Jersey is best
suited for creation of a REST API, so the expectation would be that the front end is
independent.