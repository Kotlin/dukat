interface IoNull {}
interface IoPipe {}

interface IoOptions<
  IoIn extends IoNull | IoPipe,
  IoOut extends IoNull | IoPipe,
  IoErr extends IoNull | IoPipe,
  > {}