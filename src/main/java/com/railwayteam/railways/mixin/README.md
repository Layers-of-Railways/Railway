# IMPORTANT:
~~You have to be very careful what Registrate-involved classes
you import from Create. They can cause data generators to fail.~~
The above is probably wrong. It is most likely the fact that we
reference `AllTags` classes that causes the issue. I don't know
if the following advice is therefore still applicable.
>If possible, `import static` just the values you need.
> 
>*Also, you can delete `src/generated/resources/data` and re-run `runData`,
this tends to fix it* **Make sure to add `src/generated/resources/data` back
> into git before sending in a commit if you use this trick.**