package com.example

data class PeriodicElement(
    val number: Int,
    val symbol: String,
    val name: String,
    val weight: String,
    val group: Int,
    val period: Int,
    val category: String,
    val config: String,
    val phase: String, // Gas, Liquid, Solid, Synthetic
    val gridX: Int, // 1 to 18
    val gridY: Int, // 1 to 10
    val description: String = ""
)

object PeriodicTableData {
    val elements = listOf(
        // Period 1
        PeriodicElement(1, "H", "Hydrogen", "1.008", 1, 1, "Nonmetal", "1s¹", "Gas", 1, 1, "The lightest, most abundant element in the universe. Extremely flammable."),
        PeriodicElement(2, "He", "Helium", "4.0026", 18, 1, "Noble Gas", "1s²", "Gas", 18, 1, "Colorless, odorless, inert noble gas. Used in cryogenics and balloons."),

        // Period 2
        PeriodicElement(3, "Li", "Lithium", "6.94", 1, 2, "Alkali Metal", "[He] 2s¹", "Solid", 1, 2, "The lightest metal. Highly reactive, used in rechargeable batteries."),
        PeriodicElement(4, "Be", "Beryllium", "9.0122", 2, 2, "Alkaline Earth", "[He] 2s²", "Solid", 2, 2, "Strong, lightweight, toxic metal. Used in aerospace and spacecraft windows."),
        PeriodicElement(5, "B", "Boron", "10.81", 13, 2, "Metalloid", "[He] 2s² 2p¹", "Solid", 13, 2, "Metalloid used in borosilicate glassware, magnets, and semiconductors."),
        PeriodicElement(6, "C", "Carbon", "12.011", 14, 2, "Nonmetal", "[He] 2s² 2p²", "Solid", 14, 2, "The basis of organic chemistry and all known life. Occurs as graphite and diamond."),
        PeriodicElement(7, "N", "Nitrogen", "14.007", 15, 2, "Nonmetal", "[He] 2s² 2p³", "Gas", 15, 2, "Makes up 78% of Earth's atmosphere. Crucial for life and nitrogen fertilizers."),
        PeriodicElement(8, "O", "Oxygen", "15.999", 16, 2, "Nonmetal", "[He] 2s² 2p⁴", "Gas", 16, 2, "Highly reactive gas essential for respiration in most living organisms."),
        PeriodicElement(9, "F", "Fluorine", "18.998", 17, 2, "Halogen", "[He] 2s² 2p⁵", "Gas", 17, 2, "The most chemically reactive and electronegative of all elements."),
        PeriodicElement(10, "Ne", "Neon", "20.180", 18, 2, "Noble Gas", "[He] 2s² 2p⁶", "Gas", 18, 2, "Inert gas famous for its reddish-orange glow in high-voltage neon signs."),

        // Period 3
        PeriodicElement(11, "Na", "Sodium", "22.990", 1, 3, "Alkali Metal", "[Ne] 3s¹", "Solid", 1, 3, "Soft, silvery alkali metal. Reacts violently with water. Key component of salt."),
        PeriodicElement(12, "Mg", "Magnesium", "24.305", 2, 3, "Alkaline Earth", "[Ne] 3s²", "Solid", 2, 3, "Lightweight metal. Burns with a brilliant white light. Essential mineral for life."),
        PeriodicElement(13, "Al", "Aluminium", "26.982", 13, 3, "Post-Transition Metal", "[Ne] 3s² 3p¹", "Solid", 13, 3, "Lightweight, non-magnetic, corrosion-resistant metal. Widely recycled."),
        PeriodicElement(14, "Si", "Silicon", "28.085", 14, 3, "Metalloid", "[Ne] 3s² 3p²", "Solid", 14, 3, "Abundant metalloid. Foundation of the semiconductor and computing industry."),
        PeriodicElement(15, "P", "Phosphorus", "30.974", 15, 3, "Nonmetal", "[Ne] 3s² 3p³", "Solid", 15, 3, "Highly reactive nonmetal. Red phosphorus is used in safety matches."),
        PeriodicElement(16, "S", "Sulfur", "32.06", 16, 3, "Nonmetal", "[Ne] 3s² 3p⁴", "Solid", 16, 3, "Bright yellow crystalline solid. Emits a strong smell when burned."),
        PeriodicElement(17, "Cl", "Chlorine", "35.45", 17, 3, "Halogen", "[Ne] 3s² 3p⁵", "Gas", 17, 3, "Pale green toxic gas. Used as a powerful disinfectant and in table salt."),
        PeriodicElement(18, "Ar", "Argon", "39.948", 18, 3, "Noble Gas", "[Ne] 3s² 3p⁶", "Gas", 18, 3, "The third most abundant gas in Earth's atmosphere. Highly inert."),

        // Period 4
        PeriodicElement(19, "K", "Potassium", "39.098", 1, 4, "Alkali Metal", "[Ar] 4s¹", "Solid", 1, 4, "Extremely reactive, soft metal. Vital electrolyte for biological functions."),
        PeriodicElement(20, "Ca", "Calcium", "40.078", 2, 4, "Alkaline Earth", "[Ar] 4s²", "Solid", 2, 4, "Essential mineral for bone health, tooth structure, and cellular signalling."),
        PeriodicElement(21, "Sc", "Scandium", "44.956", 3, 4, "Transition Metal", "[Ar] 3d¹ 4s²", "Solid", 3, 4, "Rare transition metal. Used in aluminum-scandium alloys for high strength."),
        PeriodicElement(22, "Ti", "Titanium", "47.867", 4, 4, "Transition Metal", "[Ar] 3d² 4s²", "Solid", 4, 4, "Extremely strong, lightweight, corrosion-resistant transition metal."),
        PeriodicElement(23, "V", "Vanadium", "50.942", 5, 4, "Transition Metal", "[Ar] 3d³ 4s²", "Solid", 5, 4, "Hard, ductile transition metal. Primarily used as an additive in steel alloys."),
        PeriodicElement(24, "Cr", "Chromium", "51.996", 6, 4, "Transition Metal", "[Ar] 3d⁵ 4s¹", "Solid", 6, 4, "Steely-gray, lustrous metal. Valued for its high corrosion resistance and shine."),
        PeriodicElement(25, "Mn", "Manganese", "54.938", 7, 4, "Transition Metal", "[Ar] 3d⁵ 4s²", "Solid", 7, 4, "Essential for steel production, battery manufacture, and biological enzymes."),
        PeriodicElement(26, "Fe", "Iron", "55.845", 8, 4, "Transition Metal", "[Ar] 3d⁶ 4s²", "Solid", 8, 4, "The most common element on Earth by mass. Core component of steel and blood hemoglobin."),
        PeriodicElement(27, "Co", "Cobalt", "58.933", 9, 4, "Transition Metal", "[Ar] 3d⁷ 4s²", "Solid", 9, 4, "Ferromagnetic metal. Vital component of lithium-ion battery cathodes."),
        PeriodicElement(28, "Ni", "Nickel", "58.693", 10, 4, "Transition Metal", "[Ar] 3d⁸ 4s²", "Solid", 10, 4, "Silvery-white metal. Resists corrosion, extensively used in plating and stainless steel."),
        PeriodicElement(29, "Cu", "Copper", "63.546", 11, 4, "Transition Metal", "[Ar] 3d¹⁰ 4s¹", "Solid", 11, 4, "Soft, malleable, ductile metal with exceptional electrical and thermal conductivity."),
        PeriodicElement(30, "Zn", "Zinc", "65.38", 12, 4, "Transition Metal", "[Ar] 3d¹⁰ 4s²", "Solid", 12, 4, "Silvery-blue metal. Widely used to galvanize iron and steel against rusting."),
        PeriodicElement(31, "Ga", "Gallium", "69.723", 13, 4, "Post-Transition Metal", "[Ar] 3d¹⁰ 4s² 4p¹", "Solid", 13, 4, "Melts at just 29.76°C. Solid gallium melts on your palm. Used in LEDs."),
        PeriodicElement(32, "Ge", "Germanium", "72.63", 14, 4, "Metalloid", "[Ar] 3d¹⁰ 4s² 4p²", "Solid", 14, 4, "Lustrous gray metalloid. Highly valued in infrared optics and fiber optics."),
        PeriodicElement(33, "As", "Arsenic", "74.922", 15, 4, "Metalloid", "[Ar] 3d¹⁰ 4s² 4p³", "Solid", 15, 4, "Metalloid notoriously famous for its extreme toxicity and use as a poison."),
        PeriodicElement(34, "Se", "Selenium", "78.971", 16, 4, "Nonmetal", "[Ar] 3d¹⁰ 4s² 4p⁴", "Solid", 16, 4, "Photoconductive nonmetal. Essential nutrient in tiny amounts but toxic in excess."),
        PeriodicElement(35, "Br", "Bromine", "79.904", 17, 4, "Halogen", "[Ar] 3d¹⁰ 4s² 4p⁵", "Liquid", 17, 4, "The only nonmetallic element that is a dark-red liquid at standard room temperature."),
        PeriodicElement(36, "Kr", "Krypton", "83.798", 18, 4, "Noble Gas", "[Ar] 3d¹⁰ 4s² 4p⁶", "Gas", 18, 4, "Noble gas used in high-speed flash photography and high-performance energy-saving windows."),

        // Period 5
        PeriodicElement(37, "Rb", "Rubidium", "85.468", 1, 5, "Alkali Metal", "[Kr] 5s¹", "Solid", 1, 5, "Soft, extremely reactive metal. Ignites spontaneously in air and reacts violently in water."),
        PeriodicElement(38, "Sr", "Strontium", "87.62", 2, 5, "Alkaline Earth", "[Kr] 5s²", "Solid", 2, 5, "Highly reactive alkaline earth metal. Gives fireworks their brilliant crimson red color."),
        PeriodicElement(39, "Y", "Yttrium", "88.906", 3, 5, "Transition Metal", "[Kr] 4d¹ 5s²", "Solid", 3, 5, "Transition metal used as a catalyst and in lasers (YAG - Yttrium Aluminum Garnet)."),
        PeriodicElement(40, "Zr", "Zirconium", "91.224", 4, 5, "Transition Metal", "[Kr] 4d² 5s²", "Solid", 4, 5, "Highly corrosion-resistant metal. Widely used in cladding for nuclear fuel rods."),
        PeriodicElement(41, "Nb", "Niobium", "92.906", 5, 5, "Transition Metal", "[Kr] 4d⁴ 5s¹", "Solid", 5, 5, "Superconducting metal. Used extensively in super-strong superconducting magnets."),
        PeriodicElement(42, "Mo", "Molybdenum", "95.95", 6, 5, "Transition Metal", "[Kr] 4d⁵ 5s¹", "Solid", 6, 5, "Transition metal with a very high melting point. Critical steel strengthener."),
        PeriodicElement(43, "Tc", "Technetium", "98", 7, 5, "Transition Metal", "[Kr] 4d⁵ 5s²", "Synthetic", 7, 5, "The lightest chemical element with no stable isotopes. Highly radioactive, used in medical scans."),
        PeriodicElement(44, "Ru", "Ruthenium", "101.07", 8, 5, "Transition Metal", "[Kr] 4d⁷ 5s¹", "Solid", 8, 5, "Rare platinum group transition metal. Highly resistant to chemical attacks."),
        PeriodicElement(45, "Rh", "Rhodium", "102.91", 9, 5, "Transition Metal", "[Kr] 4d⁸ 5s¹", "Solid", 9, 5, "Extremely rare and highly valuable noble metal. Primary catalyst in catalytic converters."),
        PeriodicElement(46, "Pd", "Palladium", "106.42", 10, 5, "Transition Metal", "[Kr] 4d¹⁰", "Solid", 10, 5, "Lustrous white metal. Absorbs up to 900 times its own volume of hydrogen gas."),
        PeriodicElement(47, "Ag", "Silver", "107.87", 11, 5, "Transition Metal", "[Kr] 4d¹⁰ 5s¹", "Solid", 11, 5, "Possesses the highest electrical and thermal conductivity of all known metals."),
        PeriodicElement(48, "Cd", "Cadmium", "112.41", 12, 5, "Transition Metal", "[Kr] 4d¹⁰ 5s²", "Solid", 12, 5, "Soft, toxic metal. Used as a neutron absorber in nuclear power reactors."),
        PeriodicElement(49, "In", "Indium", "114.82", 13, 5, "Post-Transition Metal", "[Kr] 4d¹⁰ 5s² 5p¹", "Solid", 13, 5, "Very soft metal. Essential as Indium Tin Oxide (ITO) for touchscreens and LCDs."),
        PeriodicElement(50, "Sn", "Tin", "118.71", 14, 5, "Post-Transition Metal", "[Kr] 4d¹⁰ 5s² 5p²", "Solid", 14, 5, "Silvery metal. Resists corrosion. Alloyed with copper to create ancient bronze."),
        PeriodicElement(51, "Sb", "Antimony", "121.76", 15, 5, "Metalloid", "[Kr] 4d¹⁰ 5s² 5p³", "Solid", 15, 5, "Lustrous gray metalloid. Extensively used in microelectronics and lead-acid batteries."),
        PeriodicElement(52, "Te", "Tellurium", "127.60", 16, 5, "Metalloid", "[Kr] 4d¹⁰ 5s² 5p⁴", "Solid", 16, 5, "Rare, brittle silver-white metalloid. Widely used in solar panels (cadmium telluride)."),
        PeriodicElement(53, "I", "Iodine", "126.90", 17, 5, "Halogen", "[Kr] 4d¹⁰ 5s² 5p⁵", "Solid", 17, 5, "Dark-purple, lustrous halogen. Key dietary nutrient. Sublimes into violet gas."),
        PeriodicElement(54, "Xe", "Xenon", "131.29", 18, 5, "Noble Gas", "[Kr] 4d¹⁰ 5s² 5p⁶", "Gas", 18, 5, "Heavy noble gas. Used in specialized searchlights, camera flashes, and ion thrusters."),

        // Period 6
        PeriodicElement(55, "Cs", "Cesium", "132.91", 1, 6, "Alkali Metal", "[Xe] 6s¹", "Solid", 1, 6, "Soft, gold-colored reactive metal. Cesium-133 vibrations define the standard second."),
        PeriodicElement(56, "Ba", "Barium", "137.33", 2, 6, "Alkaline Earth", "[Xe] 6s²", "Solid", 2, 6, "Soft, silvery alkaline earth metal. Used in drilling fluids and medical imaging."),
        // Lanthanides (separate rows at gridY = 9, gridX = 3..17)
        PeriodicElement(57, "La", "Lanthanum", "138.91", 3, 6, "Lanthanide", "[Xe] 5d¹ 6s²", "Solid", 3, 9, "Gives its name to the lanthanide series. Used in hydrogen storage, batteries, and lenses."),
        PeriodicElement(58, "Ce", "Cerium", "140.12", 3, 6, "Lanthanide", "[Xe] 4f¹ 5d¹ 6s²", "Solid", 4, 9, "Most abundant rare-earth metal. Major component of lighter flints (mischmetal)."),
        PeriodicElement(59, "Pr", "Praseodymium", "140.91", 3, 6, "Lanthanide", "[Xe] 4f³ 6s²", "Solid", 5, 9, "Silvery rare earth. Gives glass and goggles an intense yellow-green color."),
        PeriodicElement(60, "Nd", "Neodymium", "144.24", 3, 6, "Lanthanide", "[Xe] 4f⁴ 6s²", "Solid", 6, 9, "Strongest known permanent magnet material. Indispensable in speakers and hard drives."),
        PeriodicElement(61, "Pm", "Promethium", "145", 3, 6, "Lanthanide", "[Xe] 4f⁵ 6s²", "Synthetic", 7, 9, "Extremely rare and radioactive lanthanide. Used in luminous paints and atomic batteries."),
        PeriodicElement(62, "Sm", "Samarium", "150.36", 3, 6, "Lanthanide", "[Xe] 4f⁶ 6s²", "Solid", 8, 9, "Rare earth metal. Samarium-cobalt magnets are highly resistant to demagnetization."),
        PeriodicElement(63, "Eu", "Europium", "151.96", 3, 6, "Lanthanide", "[Xe] 4f⁷ 6s²", "Solid", 9, 9, "The most reactive rare-earth element. Key red phosphor used in neon, LED, and TV screens."),
        PeriodicElement(64, "Gd", "Gadolinium", "157.25", 3, 6, "Lanthanide", "[Xe] 4f⁷ 5d¹ 6s²", "Solid", 10, 9, "Rare earth with high thermal neutron absorption. Key MRI contrast agent."),
        PeriodicElement(65, "Tb", "Terbium", "158.93", 3, 6, "Lanthanide", "[Xe] 4f⁹ 6s²", "Solid", 11, 9, "Silvery rare earth metal. Key component of green phosphors and Terfenol-D alloy."),
        PeriodicElement(66, "Dy", "Dysprosium", "162.50", 3, 6, "Lanthanide", "[Xe] 4f¹⁰ 6s²", "Solid", 12, 9, "Lustrous rare earth metal with high magnetic susceptibility. Used in hybrid vehicles."),
        PeriodicElement(67, "Ho", "Holmium", "164.93", 3, 6, "Lanthanide", "[Xe] 4f¹¹ 6s²", "Solid", 13, 9, "Rare earth with the highest magnetic moment of any element. Used in high-strength magnets."),
        PeriodicElement(68, "Er", "Erbium", "167.26", 3, 6, "Lanthanide", "[Xe] 4f¹² 6s²", "Solid", 14, 9, "Silvery-pink rare earth metal. Amplifies fiber-optic communications signals."),
        PeriodicElement(69, "Tm", "Thulium", "168.93", 3, 6, "Lanthanide", "[Xe] 4f¹³ 6s²", "Solid", 15, 9, "The least abundant stable lanthanide. Used in portable x-ray machines and lasers."),
        PeriodicElement(70, "Yb", "Ytterbium", "173.05", 3, 6, "Lanthanide", "[Xe] 4f¹⁴ 6s²", "Solid", 16, 9, "Rare earth used in steel stress gauges and atomic clocks for extreme precision."),
        PeriodicElement(71, "Lu", "Lutetium", "174.97", 3, 6, "Lanthanide", "[Xe] 4f¹⁴ 5d¹ 6s²", "Solid", 17, 9, "The densest and hardest rare-earth element. Used in cancer therapies and CAT scans."),
        // Back to main row 6 (gridY = 6)
        PeriodicElement(72, "Hf", "Hafnium", "178.49", 4, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d² 6s²", "Solid", 4, 6, "Lustrous transition metal. Absorbs neutrons exceptionally well, used in reactor control rods."),
        PeriodicElement(73, "Ta", "Tantalum", "180.95", 5, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d³ 6s²", "Solid", 5, 6, "Highly corrosion-resistant gray transition metal. Crucial for high-grade capacitors in phones."),
        PeriodicElement(74, "W", "Tungsten", "183.84", 6, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d⁴ 6s²", "Solid", 6, 6, "Has the highest melting point of all elements (3,422°C). Used in rocket nozzles and filaments."),
        PeriodicElement(75, "Re", "Rhenium", "186.21", 7, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d⁵ 6s²", "Solid", 7, 6, "Extremely rare transition metal with high heat tolerance. Vital for jet engine turbine blades."),
        PeriodicElement(76, "Os", "Osmium", "190.23", 8, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d⁶ 6s²", "Solid", 8, 6, "The densest naturally occurring element on Earth. Brittle, silvery-blue metal."),
        PeriodicElement(77, "Ir", "Iridium", "192.22", 9, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d⁷ 6s²", "Solid", 9, 6, "The most corrosion-resistant metal. Famously rich in asteroid impact clay layers."),
        PeriodicElement(78, "Pt", "Platinum", "195.08", 10, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d⁹ 6s¹", "Solid", 10, 6, "Precious metal of outstanding resistance to chemical attack. Extremely rare."),
        PeriodicElement(79, "Au", "Gold", "196.97", 11, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s¹", "Solid", 11, 6, "Highly ductile, non-reactive precious metal. Symbol of wealth and currency since antiquity."),
        PeriodicElement(80, "Hg", "Mercury", "200.59", 12, 6, "Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s²", "Liquid", 12, 6, "The only metal that is liquid at standard room temperature. Heavy and toxic."),
        PeriodicElement(81, "Tl", "Thallium", "204.38", 13, 6, "Post-Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p¹", "Solid", 13, 6, "Soft gray metal. High compounds toxicity. Historically used as a rodenticide."),
        PeriodicElement(82, "Pb", "Lead", "207.2", 14, 6, "Post-Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p²", "Solid", 14, 6, "Heavy, soft, malleable metal. Shields radiation effectively but is highly toxic to ingest."),
        PeriodicElement(83, "Bi", "Bismuth", "208.98", 15, 6, "Post-Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p³", "Solid", 15, 6, "Heavy metal with low toxicity. Forms beautiful iridescent oxide crystals."),
        PeriodicElement(84, "Po", "Polonium", "209", 16, 6, "Post-Transition Metal", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p⁴", "Solid", 16, 6, "Highly radioactive, rare element. Emits intense alpha decay heat."),
        PeriodicElement(85, "At", "Astatine", "210", 17, 6, "Halogen", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p⁵", "Solid", 17, 6, "The rarest naturally occurring element on Earth's crust. Highly radioactive."),
        PeriodicElement(86, "Rn", "Radon", "222", 18, 6, "Noble Gas", "[Xe] 4f¹⁴ 5d¹⁰ 6s² 6p⁶", "Gas", 18, 6, "Radioactive noble gas. Accumulates in basements, second leading cause of lung cancer."),

        // Period 7
        PeriodicElement(87, "Fr", "Francium", "223", 1, 7, "Alkali Metal", "[Rn] 7s¹", "Solid", 1, 7, "Extremely rare and unstable alkali metal. Highly radioactive, disintegrates rapidly."),
        PeriodicElement(88, "Ra", "Radium", "226", 2, 7, "Alkaline Earth", "[Rn] 7s²", "Solid", 2, 7, "Intensely radioactive metal. Famously discovered by Marie and Pierre Curie."),
        // Actinides (separate rows at gridY = 10, gridX = 3..17)
        PeriodicElement(89, "Ac", "Actinium", "227", 3, 7, "Actinide", "[Rn] 6d¹ 7s²", "Solid", 3, 10, "Radioactive metal that glows in the dark with a blue light. Start of actinide series."),
        PeriodicElement(90, "Th", "Thorium", "232.04", 3, 7, "Actinide", "[Rn] 6d² 7s²", "Solid", 4, 10, "Radioactive metal. Highly promising alternative fuel source to uranium in nuclear power."),
        PeriodicElement(91, "Pa", "Protactinium", "231.04", 3, 7, "Actinide", "[Rn] 5f² 6d¹ 7s²", "Solid", 5, 10, "Extremely toxic, scarce, and highly radioactive element, with no current practical use."),
        PeriodicElement(92, "U", "Uranium", "238.03", 3, 7, "Actinide", "[Rn] 5f³ 6d¹ 7s²", "Solid", 6, 10, "Very dense, radioactive metal. Essential fuel for nuclear reactors and weapon systems."),
        PeriodicElement(93, "Np", "Neptunium", "237", 3, 7, "Actinide", "[Rn] 5f⁴ 6d¹ 7s²", "Solid", 7, 10, "Radioactive transuranic metal. Found in trace amounts, byproduct of uranium fission."),
        PeriodicElement(94, "Pu", "Plutonium", "244", 3, 7, "Actinide", "[Rn] 5f⁶ 7s²", "Solid", 8, 10, "Fissile radioactive metal. Used in nuclear reactors and weapons. Synthesized in labs."),
        PeriodicElement(95, "Am", "Americium", "243", 3, 7, "Actinide", "[Rn] 5f⁷ 7s²", "Synthetic", 9, 10, "Radioactive synthetic element. Famously used in residential smoke detectors."),
        PeriodicElement(96, "Cm", "Curium", "247", 3, 7, "Actinide", "[Rn] 5f⁷ 6d¹ 7s²", "Synthetic", 10, 10, "Hard, dense synthetic actinide. Extremely radioactive, glows in the dark."),
        PeriodicElement(97, "Bk", "Berkelium", "247", 3, 7, "Actinide", "[Rn] 5f⁹ 7s²", "Synthetic", 11, 10, "Synthetic radioactive element named after Berkeley, California. Has no commercial uses."),
        PeriodicElement(98, "Cf", "Californium", "251", 3, 7, "Actinide", "[Rn] 5f¹⁰ 7s²", "Synthetic", 12, 10, "Synthetic radioactive metal. Highly active neutron emitter used to start nuclear reactors."),
        PeriodicElement(99, "Es", "Einsteinium", "252", 3, 7, "Actinide", "[Rn] 5f¹¹ 7s²", "Synthetic", 13, 10, "Radioactive synthetic metal named after Albert Einstein. Discovered in thermonuclear fallout."),
        PeriodicElement(100, "Fm", "Fermium", "257", 3, 7, "Actinide", "[Rn] 5f¹² 7s²", "Synthetic", 14, 10, "Highly radioactive synthetic element named after nuclear physicist Enrico Fermi."),
        PeriodicElement(101, "Md", "Mendelevium", "258", 3, 7, "Actinide", "[Rn] 5f¹³ 7s²", "Synthetic", 15, 10, "Synthetic radioactive element named after periodic table pioneer Dmitri Mendeleev."),
        PeriodicElement(102, "No", "Nobelium", "259", 3, 7, "Actinide", "[Rn] 5f¹⁴ 7s²", "Synthetic", 16, 10, "Synthetic radioactive element named after Alfred Nobel. Produced in tiny quantities."),
        PeriodicElement(103, "Lr", "Lawrencium", "262", 3, 7, "Actinide", "[Rn] 5f¹⁴ 7s² 7p¹", "Synthetic", 17, 10, "Synthetic radioactive element named after cyclotron inventor Ernest Lawrence."),
        // Back to main row 7 (gridY = 7)
        PeriodicElement(104, "Rf", "Rutherfordium", "267", 4, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d² 7s²", "Synthetic", 4, 7, "Highly radioactive synthetic transuranic element. Named after Ernest Rutherford."),
        PeriodicElement(105, "Db", "Dubnium", "268", 5, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d³ 7s²", "Synthetic", 5, 7, "Highly radioactive synthetic element. Named after Dubna, Russia."),
        PeriodicElement(106, "Sg", "Seaborgium", "269", 6, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁴ 7s²", "Synthetic", 6, 7, "Synthetic transition metal named after Glenn Seaborg. Disintegrates rapidly."),
        PeriodicElement(107, "Bh", "Bohrium", "270", 7, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁵ 7s²", "Synthetic", 7, 7, "Highly radioactive synthetic transition metal named after Niels Bohr."),
        PeriodicElement(108, "Hs", "Hassium", "277", 8, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁶ 7s²", "Synthetic", 8, 7, "Synthetic transuranic transition metal named after the German state of Hesse."),
        PeriodicElement(109, "Mt", "Meitnerium", "278", 9, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁷ 7s²", "Synthetic", 9, 7, "Extremely radioactive synthetic element named after physicist Lise Meitner."),
        PeriodicElement(110, "Ds", "Darmstadtium", "281", 10, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁸ 7s²", "Synthetic", 10, 7, "Synthetic element named after Darmstadt, Germany. Highly unstable."),
        PeriodicElement(111, "Rg", "Roentgenium", "282", 11, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d⁹ 7s²", "Synthetic", 11, 7, "Synthetic transition metal named after x-ray discoverer Wilhelm Röntgen."),
        PeriodicElement(112, "Cn", "Copernicium", "285", 12, 7, "Transition Metal", "[Rn] 5f¹⁴ 6d¹⁰ 7s²", "Synthetic", 12, 7, "Extremely volatile synthetic transition metal. Named after Nicolaus Copernicus."),
        PeriodicElement(113, "Nh", "Nihonium", "286", 13, 7, "Post-Transition Metal", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p¹", "Synthetic", 13, 7, "Synthetic post-transition metal. Named after Nihon (Japan) where it was discovered."),
        PeriodicElement(114, "Fl", "Flerovium", "289", 14, 7, "Post-Transition Metal", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p²", "Synthetic", 14, 7, "Extremely radioactive synthetic element. Named after Flerov Laboratory of Nuclear Reactions."),
        PeriodicElement(115, "Mc", "Moscovium", "290", 15, 7, "Post-Transition Metal", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p³", "Synthetic", 15, 7, "Highly radioactive synthetic element named after Moscow Oblast, Russia."),
        PeriodicElement(116, "Lv", "Livermorium", "293", 16, 7, "Post-Transition Metal", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p⁴", "Synthetic", 16, 7, "Highly radioactive synthetic transuranic element. Named after Lawrence Livermore Lab."),
        PeriodicElement(117, "Ts", "Tennessine", "294", 17, 7, "Halogen", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p⁵", "Synthetic", 17, 7, "Synthetic halogen named after Tennessee, US. Half-life is in milliseconds."),
        PeriodicElement(118, "Og", "Oganesson", "294", 18, 7, "Noble Gas", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p⁶", "Synthetic", 18, 7, "The heaviest synthetic chemical element. Named after Yuri Oganessian.")
    )
}
