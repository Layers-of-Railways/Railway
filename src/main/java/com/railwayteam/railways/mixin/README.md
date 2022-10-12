# IMPORTANT:
You have to be very careful what Registrate-involved classes
you import from Create. They can cause data generators to fail.
If possible, `import static` just the values you need.

*Also, you can delete `src/generated/resources/data` and re-run `runData`,
this tends to fix it*