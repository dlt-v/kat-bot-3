# Environment variables

This file lists the environment variables the application expects and where they are used. Variable names are case-sensitive; use the exact names shown below.

- `token` — Required. Discord bot token used to authenticate with Discord (used in `KatBotConfig`).
- `DB_URL` — JDBC URL for Postgres (used in `KatBotConfig`).
- `DB_USERNAME` — Database username (used in `KatBotConfig` and `docker-compose.yml`).
- `DB_PASSWORD` — Database password (used in `KatBotConfig` and `docker-compose.yml`).
- `DB_SCHEMA` — Optional. Database schema for the DataSource (used in `KatBotConfig`).
- `DB_NAME` — Used by `docker-compose.yml` to initialize the Postgres database.
- `environment` — Optional. Set to `testing` to enable test mode; defaults to `production` if omitted or invalid (used in `ParameterService` and `TestModeChannelValidator`).
- `testing-channel-id` — Required when `environment=testing`: the channel ID where the bot should respond in test mode.
- `testing-user-id` — Optional but recommended: used to fetch footer/avatar in help/about commands.
- `version` — Optional: displayed in help/about; if unset it shows `not set`.

See the `.env.example` file in the project root for a template you can copy.
