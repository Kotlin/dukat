
interface ConfigurationBoolean {
  'dry-run': boolean;
  'upper-case-expansion': boolean;
  'combine-lists': boolean;
  'dot-dot-notation': boolean;
}

interface ExtendeConfigurationBoolean {
  'dry-run': boolean;
  'upper-case-expansion': boolean;
  'combine-lists': boolean;
  'dot-dot-notation': boolean;

  valid_field: boolean;
  other_valid_field: string;
}

interface HeterogenousConfig {
  'dry-run': boolean;
  'upper-case-expansion': string;
  'combine-lists': Array<boolean>;
  'dot-dot-notation': boolean;

  valid_field: boolean;
  other_valid_field: string;
}
